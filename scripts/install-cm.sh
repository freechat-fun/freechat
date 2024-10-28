#!/usr/bin/env bash

source $(dirname ${BASH_SOURCE[0]})/setenv.sh

check_kubectl
check_helm

helm repo add jetstack https://charts.jetstack.io &>/dev/null
helm repo update
helm --kubeconfig ${KUBE_CONFIG} install --create-namespace -f ${values_yaml} \
  --namespace ${HELM_cert_manager_namespace} \
  --version ${HELM_cert_manager_version} \
  --set installCRDs=true \
  cert-manager jetstack/cert-manager

CLUSTER_ISSUER_YAML=$(mktemp -d)/clusterissuer.yaml

cat > ${CLUSTER_ISSUER_YAML} <<EOF
apiVersion: cert-manager.io/v1
kind: ClusterIssuer
metadata:
  annotations:
  labels:
    app.kubernetes.io/instance:  ${HELM_name}-cm
    app.kubernetes.io/name:  ${HELM_name}
    app.kubernetes.io/version: ${HELM_appVersion}
  name: ${HELM_name}-letsencrypt-http01
  resourceVersion: "120055"
  uid: 06f1a20b-92cd-444c-afa7-bee7f7b25047
spec:
  acme:
    email: ${HELM_maintainers_email}
    privateKeySecretRef:
      name: ${HELM_name}-letsencrypt-http01-key
    server: https://acme-v02.api.letsencrypt.org/directory
    solvers:
    - http01:
        ingress:
          class: ${HELM_ingress_className}
EOF

if [[ "${VERBOSE}" == "1" ]];then
  echo "[clusterissuer.yaml]"
  cat ${CLUSTER_ISSUER_YAML}
fi

kubectl --kubeconfig ${KUBE_CONFIG} -f ${CLUSTER_ISSUER_YAML}
