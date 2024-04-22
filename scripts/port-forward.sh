#!/usr/bin/env bash

source $(dirname ${BASH_SOURCE[0]})/setenv.sh

check_kubectl

function stop(){
  exit 0
}

trap stop SIGINT

while true;do
  pod=$(kubectl get pods -o name --kubeconfig ${KUBE_CONFIG} --namespace ${NAMESPACE} \
  | awk -F'/' '{print $2}'| grep "${PROJECT_NAME}-main" | head -1)

  test -n "${pod}" || die "ERROR: Failed to find app pod!"

  kubectl port-forward --kubeconfig ${KUBE_CONFIG} --namespace ${NAMESPACE} \
    pod/${pod} ${HELM_debug_jpda_port}:${HELM_debug_jpda_port}
done
