#!/usr/bin/env bash

source $(dirname ${BASH_SOURCE[0]})/setenv.sh

check_helm

helm upgrade --kubeconfig ${KUBE_CONFIG} --namespace ${NAMESPACE} --create-namespace -f ${values_yaml} \
  --set bitnami.milvus.enabled=false \
  --set bitnami.mysql.enabled=false \
  --set bitnami.redis.enabled=false \
  --set bitnami.prometheus.enabled=false \
  --set bitnami.grafana.enabled=false \
  --set cert.clusterIssuer.enabled=false \
  --set deployment.backend.enabled=false \
  --set deployment.frontend.enabled=false \
  --set deployment.pvc.enabled=true \
  ${ARGS[*]} \
  ${PROJECT_NAME}-pvc ${HELM_CONFIG_HOME}
