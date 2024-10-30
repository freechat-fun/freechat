#!/usr/bin/env bash

source $(dirname ${BASH_SOURCE[0]})/setenv.sh

check_helm

helm install --kubeconfig ${KUBE_CONFIG} --namespace ${NAMESPACE} --create-namespace -f ${values_yaml} \
  --set mysql.enabled=false \
  --set redis.enabled=false \
  --set milvus.enabled=false \
  --set prometheus.enabled=false \
  --set grafana.enabled=false \
  --set grafana-loki.enabled=false \
  --set kube-state-metrics.enabled=false \
  --set backend.enabled=false \
  --set frontend.enabled=true \
  --set persistence.enabled=false \
  ${ARGS[*]} \
  ${PROJECT_NAME}-web ${HELM_CONFIG_HOME}
