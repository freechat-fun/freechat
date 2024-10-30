#!/usr/bin/env bash

source $(dirname ${BASH_SOURCE[0]})/setenv.sh

check_helm

helm upgrade --kubeconfig ${KUBE_CONFIG} --namespace ${NAMESPACE} --create-namespace -f ${values_yaml} \
  --set mysql.deployment.enabled=false \
  --set redis.deployment.enabled=false \
  --set milvus.deployment.enabled=false \
  --set prometheus.deployment.enabled=false \
  --set grafana.deployment.enabled=false \
  --set grafana-loki.deployment.enabled=false \
  --set kube-state-metrics.deployment.enabled=false \
  --set backend.enabled=false \
  --set frontend.enabled=false \
  --set persistence.enabled=true \
  ${ARGS[*]} \
  ${PROJECT_NAME}-pvc ${HELM_CONFIG_HOME}
