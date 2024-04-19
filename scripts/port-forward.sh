#!/usr/bin/env bash

source $(dirname ${BASH_SOURCE[0]})/setenv.sh

function stop(){
  exit 0
}

trap stop SIGINT

while true;do
  pod=$(kubectl get pods -o name --kubeconfig ${KUBE_CONFIG} --namespace ${NAMESPACE} \
    | awk -F'/' '{print $2}'| grep "${PROJECT_NAME}" \
    | grep -v -p "mysql" | grep -v -p "redis" | head -1)
  if [[ -z "${pod}" ]]; then
    echo "Failed to find app pod!"
    exit -1
  fi
  kubectl port-forward --kubeconfig ${KUBE_CONFIG} --namespace ${NAMESPACE} \
    pod/${pod} ${HELM_debug_jpda_port}:${HELM_debug_jpda_port}
done
