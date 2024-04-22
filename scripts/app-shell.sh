#!/usr/bin/env bash

source $(dirname ${BASH_SOURCE[0]})/setenv.sh

pod=$(kubectl get pods -o name --kubeconfig ${KUBE_CONFIG} --namespace ${NAMESPACE} \
  | awk -F'/' '{print $2}'| grep "${PROJECT_NAME}-main" | head -1)

test -n "${pod}" || die "ERROR: Failed to find app pod!"

kubectl exec --kubeconfig ${KUBE_CONFIG} --namespace ${NAMESPACE} \
  -it ${pod} -c main -- /bin/bash