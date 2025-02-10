#!/usr/bin/env bash

source $(dirname ${BASH_SOURCE[0]})/setenv.sh

check_helm

helm template --kubeconfig ${KUBE_CONFIG} --namespace ${NAMESPACE} --create-namespace -f ${values_yaml} \
  --set-file mysql.initdbScripts.1-schema\\.sql=${PROJECT_PATH}/${DAL_MODULE}/src/main/resources/sql/schema.sql \
  --set-file mysql.initdbScripts.2-data\\.sql=${PROJECT_PATH}/${DAL_MODULE}/src/main/resources/sql/data.sql \
  --set backend.enabled=true \
  --set frontend.enabled=true \
  --set mysql.enabled=true \
  --set redis.enabled=true \
  --set milvus.enabled=true \
  --set prometheus.enabled=true \
  --set grafana.enabled=true \
  --set grafana-loki.enabled=true \
  --set kube-state-metrics.enabled=true \
  ${ARGS[*]} \
  ${PROJECT_NAME} ${HELM_CONFIG_HOME}
