#!/usr/bin/env bash

source $(dirname ${BASH_SOURCE[0]})/setenv.sh

check_kubectl

function stop(){
  exit 0
}

trap stop SIGINT

while true;do
  pod=$(kubectl get pods --kubeconfig ${KUBE_CONFIG} --namespace ${NAMESPACE} \
    | grep "${PROJECT_NAME}-main" | awk -F' ' '{print $1}' | head -1)

  test -n "${pod}" || die "ERROR: Failed to find app pod!"

  echo "Found ${pod}"

  kubectl port-forward --kubeconfig ${KUBE_CONFIG} --namespace ${NAMESPACE} \
    pod/${pod} ${HELM_debug_jpda_port}:${HELM_debug_jpda_port}
done
