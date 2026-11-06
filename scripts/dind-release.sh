#!/usr/bin/env bash

source $(dirname ${BASH_SOURCE[0]})/setenv.sh

set -e

set -- ${ARGS[@]+"${ARGS[@]}"}

BRANCH=
NO_LATEST=

while [ $# -gt 0 ]
do
  case $1 in
    -b|--branch)
      [ $# -ge 2 ] || die "ERROR: The option $1 requires an argument."
      BRANCH=$2
      shift
      shift
      ;;
    --no-latest)
      NO_LATEST=1
      shift
      ;;
    --help)
      cat <<EOF
Release ${PROJECT_NAME} in a docker-in-docker pod of the Kubernetes cluster.

Options：
  -b, --branch          Branch to release, default is the repository's default branch.
      --no-latest       Only push the versioned image, do not update the 'latest' tag.
      --help            Print usage.
EOF
      exit 0
      ;;
    *)
      die "ERROR: Unknown option: $1. Run with --help to see the usage."
      ;;
  esac
done

if [[ -n "${BRANCH}" ]]; then
  if [[ ! "${BRANCH}" =~ ^[A-Za-z0-9]([A-Za-z0-9._/-]*[A-Za-z0-9])?$ || "${BRANCH}" == *..* ]]; then
    die "ERROR: Invalid branch name: ${BRANCH}"
  fi
  GIT_SYNC_CMD="git reset --hard && git fetch --all --prune && git checkout -B ${BRANCH} origin/${BRANCH}"
  echo "Releasing the branch: ${BRANCH}"
else
  GIT_SYNC_CMD="git reset --hard && git pull --ff-only"
fi

RELEASE_ARGS="--release"
if [[ -n "${NO_LATEST}" ]]; then
  RELEASE_ARGS="${RELEASE_ARGS} --no-latest"
  echo "The 'latest' image tag will not be updated."
fi

# the dind pod is managed by the sub scripts, they need the same cluster settings
KUBE_ARGS=(--kubeconfig "${KUBE_CONFIG}" --helmconfig "${HELM_CONFIG}" --namespace "${NAMESPACE}")
if [[ "${VERBOSE}" == "1" ]]; then
  KUBE_ARGS+=(-v)
fi

cleanup() {
  local status=${1:-$?}
  trap - EXIT INT TERM HUP
  echo "Deleting the dind pod..."
  bash ./uninstall-dind.sh "${KUBE_ARGS[@]}" || echo "WARN: Failed to delete the dind pod 'docker' in the namespace '${NAMESPACE}', please delete it manually."
  exit ${status}
}

bash ./install-dind.sh "${KUBE_ARGS[@]}"

# only clean up the pod created by this run
trap cleanup EXIT
trap 'cleanup 130' INT
trap 'cleanup 143' TERM
trap 'cleanup 129' HUP

sleep 10

kubectl exec --kubeconfig ${KUBE_CONFIG} --namespace ${NAMESPACE} \
  -it docker -- /bin/sh -euc "\
    apk update && \
    apk add --no-cache bash git nodejs npm openjdk25 maven && \
    docker login && \
    if [ ! -d "/${PROJECT_NAME}" ]; then git clone ${SCM_URL} /${PROJECT_NAME}; fi && \
    cd /${PROJECT_NAME}/scripts && \
    ${GIT_SYNC_CMD} && \
    ./build.sh ${RELEASE_ARGS}"
