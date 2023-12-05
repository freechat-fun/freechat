#!/bin/bash

source $(dirname ${BASH_SOURCE[0]})/setenv.sh

values_yaml="${HELM_CONFIG_HOME}/values.yaml"
if [[ -f "${HELM_CONFIG_HOME}/values-private.yaml" ]]; then
  values_yaml="${HELM_CONFIG_HOME}/values-private.yaml"
fi

helm upgrade --kubeconfig ${KUBE_CONFIG} --namespace ${NAMESPACE} -f ${values_yaml} \
  --set bitnami.mysql.enabled=false \
  --set bitnami.redis.enabled=false \
  --set cert.issuer.enabled=false \
  --set cert.certificate.enabled=false \
  --set deployment.backend.enabled=false \
  --set deployment.frontend.enabled=true \
  --set deployment.pvc.enabled=false \
  ${ARGS[*]} \
  ${PROJECT_NAME}-web ${HELM_CONFIG_HOME}
