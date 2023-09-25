#!/bin/bash

source $(dirname ${BASH_SOURCE[0]})/setenv.sh

values_yaml="${HELM_CONFIG_HOME}/values.yaml"
if [[ -f "${HELM_CONFIG_HOME}/values-private.yaml" ]]; then
  values_yaml="${HELM_CONFIG_HOME}/values-private.yaml"
fi

helm install --kubeconfig ${KUBE_CONFIG} --namespace ${NAMESPACE} --create-namespace -f ${values_yaml} \
  --set-file mysql.initdbScripts.init-schema\\.sql=${PROJECT_PATH}/${HELM_name}-dal/src/main/resources/sql/schema.sql \
  ${ARGS[*]} \
  ${PROJECT_NAME} ${HELM_CONFIG_HOME}
