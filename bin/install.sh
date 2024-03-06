#!/usr/bin/env bash

source $(dirname ${BASH_SOURCE[0]})/setenv.sh

tls_opt=""
if [[ "x${INITIAL}" == "x1" ]]; then
  ttl_opt="--set ingress.tls.enabled=false"
fi

helm install --kubeconfig ${KUBE_CONFIG} --namespace ${NAMESPACE} --create-namespace -f ${values_yaml} \
  --set-file mysql.initdbScripts.init-schema\\.sql=${PROJECT_PATH}/${HELM_name}-dal/src/main/resources/sql/schema.sql \
  --set deployment.backend.enabled=true \
  --set deployment.frontend.enabled=false \
  --set deployment.pvc.enabled=false \
  ${tls_opt} \
  ${ARGS[*]} \
  ${PROJECT_NAME} ${HELM_CONFIG_HOME}
