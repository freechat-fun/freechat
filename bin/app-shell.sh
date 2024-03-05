#!/bin/bash

source $(dirname ${BASH_SOURCE[0]})/setenv.sh

pod=$(kubectl get pods -o name --kubeconfig ${KUBE_CONFIG} --namespace ${NAMESPACE} \
  | awk -F'/' '{print $2}'| grep "${PROJECT_NAME}" \
  | grep -v -p "mysql" | grep -v -p "redis" | head -1)

if [[ -z "${pod}" ]]; then
  echo "Failed to find mysql pod!"
  exit -1
fi

kubectl exec --kubeconfig ${KUBE_CONFIG} --namespace ${NAMESPACE} \
  -it ${pod} -c ${PROJECT_NAME} -- /bin/bash