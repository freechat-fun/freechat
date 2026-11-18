#!/usr/bin/env bash
# Open an interactive shell in the main container.
# Prefers a pod that is mid-deploy (1/2), otherwise any matching pod.
#
# Usage: scripts/pod-shell.sh [--kubeconfig <path>] [-n <ns>] [-v] [--root]
#   --root  Enter as root via an ephemeral debug container:
#           - target running: join its PID namespace and chroot into its rootfs
#           - target exited:  mount the pod's PVC volume at /state instead

# Strip --root before setenv.sh parses the remaining flags.
ROOT_MODE=0
PASS=()
for a in "$@"; do
  case "$a" in
    --root) ROOT_MODE=1 ;;
    *) PASS+=("$a") ;;
  esac
done
set -- ${PASS[@]+"${PASS[@]}"}

source "$(dirname "${BASH_SOURCE[0]}")/setenv.sh"

check_kubectl

# find deploying one
pod=$(kubectl get pods --kubeconfig "${KUBE_CONFIG}" --namespace "${NAMESPACE}" \
  | grep "${PROJECT_NAME}-main" | grep "1/2" | awk -F' ' '{print $1}' | head -1)

if [[ -z "${pod}" ]]; then
  # find anyone
  pod=$(kubectl get pods --kubeconfig "${KUBE_CONFIG}" --namespace "${NAMESPACE}" \
    | grep "${PROJECT_NAME}-main" | awk -F' ' '{print $1}' | head -1)
fi

test -n "${pod}" || die "ERROR: Failed to find app pod!"

echo "Found ${pod}"

# Normal mode: plain exec as the container's configured user.
if [[ "${ROOT_MODE}" != 1 ]]; then
  kubectl exec --kubeconfig "${KUBE_CONFIG}" --namespace "${NAMESPACE}" \
    -it "${pod}" -c main -- /bin/bash
  exit $?
fi

# --- root mode ---
# kubectl exec cannot switch users; use an ephemeral debug container (root).
DEBUG_IMAGE="${DEBUG_IMAGE:-busybox:1.36}"

running=$(kubectl get pod --kubeconfig "${KUBE_CONFIG}" --namespace "${NAMESPACE}" "${pod}" \
  -o jsonpath='{.status.containerStatuses[?(@.name=="main")].state.running}')

if [[ -n "${running}" ]]; then
  # Target is running: with --target, PID 1 is the container's init process,
  # so /proc/1/root is its filesystem. chroot gives a full root shell inside it.
  echo "Entering as ROOT (chroot into running container 'main')"
  exec kubectl debug --kubeconfig "${KUBE_CONFIG}" --namespace "${NAMESPACE}" \
    -it "${pod}" --image="${DEBUG_IMAGE}" --target=main --profile=sysadmin -- \
    sh -c 'chroot /proc/1/root /bin/bash -l || { echo "chroot failed; falling back to debug shell"; exec sh; }'
else
  # Target is exited: PID-namespace sharing is unavailable (CONTAINER_EXITED).
  # Fall back to mounting the pod's PVC volume directly into the debug container.
  vol=$(kubectl get pod --kubeconfig "${KUBE_CONFIG}" --namespace "${NAMESPACE}" "${pod}" \
    -o jsonpath='{range .spec.volumes[*]}{.name}{" "}{.persistentVolumeClaim.claimName}{"\n"}{end}' \
    | awk 'NF>1 && $2!="" {print $1; exit}')
  if [[ -n "${vol}" ]]; then
    echo "Container 'main' is not running; mounting PVC volume '${vol}' at /state as ROOT"
    exec kubectl debug --kubeconfig "${KUBE_CONFIG}" --namespace "${NAMESPACE}" \
      -it "${pod}" --image="${DEBUG_IMAGE}" --profile=sysadmin \
      --custom="{\"volumeMounts\":[{\"name\":\"${vol}\",\"mountPath\":\"/state\"}]}" -- sh
  else
    echo "WARNING: 'main' is not running and no PVC volume found; plain root debug shell only"
    exec kubectl debug --kubeconfig "${KUBE_CONFIG}" --namespace "${NAMESPACE}" \
      -it "${pod}" --image="${DEBUG_IMAGE}" --profile=sysadmin -- sh
  fi
fi

