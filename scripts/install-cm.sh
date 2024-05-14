#!/usr/bin/env bash

source $(dirname ${BASH_SOURCE[0]})/setenv.sh

check_helm

helm repo add jetstack https://charts.jetstack.io &>/dev/null
helm repo update
helm --kubeconfig ${KUBE_CONFIG} install --create-namespace -f ${values_yaml} \
  --namespace ${HELM_cert_manager_namespace} \
  --version ${HELM_cert_manager_version} \
  --set installCRDs=true \
  cert-manager jetstack/cert-manager

helm --kubeconfig ${KUBE_CONFIG} install -f ${values_yaml} \
  --set bitnami.milvus.enabled=false \
  --set bitnami.mysql.enabled=false \
  --set bitnami.redis.enabled=false \
  --set bitnami.prometheus.enabled=false \
  --set bitnami.grafana.enabled=false \
  --set deployment.backend.enabled=false \
  --set deployment.frontend.enabled=false \
  --set deployment.pvc.enabled=false \
  --set cert.clusterIssuer.enabled=true \
  ${ARGS[*]} \
  ${PROJECT_NAME}-cm ${HELM_CONFIG_HOME}
