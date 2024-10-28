#!/usr/bin/env bash

source $(dirname ${BASH_SOURCE[0]})/setenv.sh

check_kubectl
check_helm

LABEL_SELECTOR="app.kubernetes.io/instance=${HELM_name}-cm"

kubectl --kubeconfig ${KUBE_CONFIG} clusterissuer -l "${LABEL_SELECTOR}"
helm uninstall --kubeconfig ${KUBE_CONFIG} --namespace ${HELM_cert_manager_namespace} cert-manager
kubectl --kubeconfig ${KUBE_CONFIG} delete -f \
  https://github.com/cert-manager/cert-manager/releases/download/${HELM_cert_manager_version}/cert-manager.crds.yaml