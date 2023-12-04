#!/bin/bash

source $(dirname ${BASH_SOURCE[0]})/setenv.sh

values_yaml="${HELM_CONFIG_HOME}/values.yaml"
if [[ -f "${HELM_CONFIG_HOME}/values-private.yaml" ]]; then
  values_yaml="${HELM_CONFIG_HOME}/values-private.yaml"
fi

helm repo add jetstack https://charts.jetstack.io &>/dev/null
helm --kubeconfig ${KUBE_CONFIG} install --create-namespace -f ${values_yaml} \
  cert-manager jetstack/cert-manager \
  --namespace ${HELM_cert_manager_namespace} \
  --version ${HELM_cert_manager_version} \
  --set installCRDs=true \
  --set deployment.backend.enabled=false \
  --set deployment.frontend.enabled=false \
  --set deployment.pvc.enabled=false
