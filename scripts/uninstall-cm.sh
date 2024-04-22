#!/usr/bin/env bash

source $(dirname ${BASH_SOURCE[0]})/setenv.sh

check_kubectl
check_helm

helm uninstall --kubeconfig ${KUBE_CONFIG} ${PROJECT_NAME}-cm
helm --kubeconfig ${KUBE_CONFIG} --namespace ${HELM_cert_manager_namespace} delete cert-manager
kubectl --kubeconfig ${KUBE_CONFIG} delete namespace ${HELM_cert_manager_namespace}
kubectl --kubeconfig ${KUBE_CONFIG} delete -f \
  https://github.com/cert-manager/cert-manager/releases/download/${HELM_cert_manager_version}/cert-manager.crds.yaml