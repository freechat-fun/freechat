#!/usr/bin/env bash

source $(dirname ${BASH_SOURCE[0]})/setenv.sh

check_kubectl

kubectl exec --kubeconfig ${KUBE_CONFIG} --namespace ${NAMESPACE} \
  -it docker -- /bin/sh
