#!/bin/bash

source $(dirname ${BASH_SOURCE[0]})/setenv.sh

values_yaml="${HELM_CONFIG_HOME}/values.yaml"
if [[ -f "${HELM_CONFIG_HOME}/values-private.yaml" ]]; then
  values_yaml="${HELM_CONFIG_HOME}/values-private.yaml"
fi

helm --kubeconfig ${KUBE_CONFIG} --namespace ${HELM_cert_manager_namespace} delete cert-manager
kubectl --kubeconfig ${KUBE_CONFIG} delete namespace ${HELM_cert_manager_namespace}
kubectl --kubeconfig ${KUBE_CONFIG} delete -f \
  https://github.com/cert-manager/cert-manager/releases/download/${HELM_cert_manager_version}/cert-manager.crds.yaml