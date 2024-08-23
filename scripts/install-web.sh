#!/usr/bin/env bash

source $(dirname ${BASH_SOURCE[0]})/setenv.sh

check_helm

helm install --kubeconfig ${KUBE_CONFIG} --namespace ${NAMESPACE} --create-namespace -f ${values_yaml} \
  --set mysql.deployment.enabled=false \
  --set redis.deployment.enabled=false \
  --set milvus.deployment.enabled=false \
  --set prometheus.deployment.enabled=false \
  --set grafana.deployment.enabled=false \
  --set grafana-loki.deployment.enabled=false \
  --set cert.clusterIssuer.enabled=false \
  --set deployment.backend.enabled=false \
  --set deployment.frontend.enabled=true \
  --set deployment.pvc.enabled=false \
  ${ARGS[*]} \
  ${PROJECT_NAME}-web ${HELM_CONFIG_HOME}
