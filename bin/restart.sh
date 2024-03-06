#!/usr/bin/env bash

source $(dirname ${BASH_SOURCE[0]})/setenv.sh

kubectl rollout restart --kubeconfig ${KUBE_CONFIG} --namespace ${NAMESPACE} \
  ${ARGS[*]} \
  deployment ${PROJECT_NAME}