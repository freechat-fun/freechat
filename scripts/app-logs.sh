#!/usr/bin/env bash

source $(dirname ${BASH_SOURCE[0]})/setenv.sh

pod=$(kubectl get pods -o name --kubeconfig ${KUBE_CONFIG} --namespace ${NAMESPACE} \
  | awk -F'/' '{print $2}'| grep "${PROJECT_NAME}-main" | head -1)

if [[ -z "${pod}" ]]; then
  echo "Failed to find mysql pod!"
  exit -1
fi

kubectl logs --kubeconfig ${KUBE_CONFIG} --namespace ${NAMESPACE} ${pod}