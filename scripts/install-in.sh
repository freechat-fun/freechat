#!/usr/bin/env bash

source $(dirname ${BASH_SOURCE[0]})/setenv.sh

helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx &>/dev/null
helm repo update

helm --kubeconfig ${KUBE_CONFIG} install --create-namespace \
  --namespace ingress-default \
  --create-namespace \
  --set controller.extraArgs.enable-ssl-passthrough=true \
  --set controller.service.externalTrafficPolicy=Local \
  ${ARGS[*]} \
  ${PROJECT_NAME} ingress-nginx/ingress-nginx
