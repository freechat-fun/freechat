#!/usr/bin/env bash

source $(dirname ${BASH_SOURCE[0]})/setenv.sh

helm upgrade --kubeconfig ${KUBE_CONFIG} --namespace ${NAMESPACE} -f ${values_yaml} \
  --set-file mysql.initdbScripts.init-schema\\.sql=${PROJECT_PATH}/${HELM_name}-dal/src/main/resources/sql/schema.sql \
  --set deployment.backend.enabled=true \
  --set deployment.frontend.enabled=false \
  --set deployment.pvc.enabled=false \
  ${ARGS[*]} \
  ${PROJECT_NAME} ${HELM_CONFIG_HOME}
