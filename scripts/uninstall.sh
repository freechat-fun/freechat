#!/usr/bin/env bash

source $(dirname ${BASH_SOURCE[0]})/setenv.sh

helm uninstall --kubeconfig ${KUBE_CONFIG} --namespace ${NAMESPACE} \
  ${ARGS[*]} \
  ${PROJECT_NAME}