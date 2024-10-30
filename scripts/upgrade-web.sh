#!/usr/bin/env bash

source $(dirname ${BASH_SOURCE[0]})/setenv.sh

check_helm

helm upgrade --kubeconfig ${KUBE_CONFIG} --namespace ${NAMESPACE} -f ${values_yaml} \
  --set mysql.deployment.enabled=false \
  --set redis.deployment.enabled=false \
  --set milvus.deployment.enabled=false \
  --set prometheus.deployment.enabled=false \
  --set grafana.deployment.enabled=false \
  --set grafana-loki.deployment.enabled=false \
  --set kube-state-metrics.deployment.enabled=false \
  --set backend.enabled=false \
  --set frontend.enabled=true \
  --set persistence.enabled=false \
  ${ARGS[*]} \
  ${PROJECT_NAME}-web ${HELM_CONFIG_HOME}
