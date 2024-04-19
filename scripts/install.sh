#!/usr/bin/env bash

source $(dirname ${BASH_SOURCE[0]})/setenv.sh

helm install --kubeconfig ${KUBE_CONFIG} --namespace ${NAMESPACE} --create-namespace -f ${values_yaml} \
  --set-file mysql.initdbScripts.1-schema\\.sql=${PROJECT_PATH}/${DAL_MODULE}/src/main/resources/sql/schema.sql \
  --set-file mysql.initdbScripts.2-data\\.sql=${PROJECT_PATH}/${DAL_MODULE}/src/main/resources/sql/data.sql \
  --set cert.clusterIssuer.enabled=false \
  --set deployment.backend.enabled=true \
  --set deployment.frontend.enabled=false \
  --set deployment.pvc.enabled=false \
  ${ARGS[*]} \
  ${PROJECT_NAME} ${HELM_CONFIG_HOME}
