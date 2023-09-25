#!/bin/bash

source $(dirname ${BASH_SOURCE[0]})/setenv.sh

if [[ " ${ARGS[*]} " =~ " --install " ]]; then
  helm repo add jetstack https://charts.jetstack.io &>/dev/null
  helm --kubeconfig ${KUBE_CONFIG} install \
    cert-manager jetstack/cert-manager \
    --namespace ${HELM_cert_manager_namespace} \
    --create-namespace \
    --version ${HELM_cert_manager_version} \
    --set installCRDs=true
elif [[ " ${ARGS[*]} " =~ " --uninstall " ]]; then
  helm --kubeconfig ${KUBE_CONFIG} --namespace ${HELM_cert_manager_namespace} delete cert-manager
  kubectl --kubeconfig ${KUBE_CONFIG} delete namespace ${HELM_cert_manager_namespace}
  kubectl --kubeconfig ${KUBE_CONFIG} delete -f \
    https://github.com/cert-manager/cert-manager/releases/download/${HELM_cert_manager_version}/cert-manager.crds.yaml
else
  kubectl --kubeconfig ${KUBE_CONFIG} get --all-namespaces \
    Issuers,ClusterIssuers,Certificates,CertificateRequests,Orders,Challenges
fi
