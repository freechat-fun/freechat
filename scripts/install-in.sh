#!/usr/bin/env bash

source $(dirname ${BASH_SOURCE[0]})/setenv.sh

check_helm

helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx &>/dev/null
helm repo update
helm --kubeconfig ${KUBE_CONFIG} install --create-namespace \
  --namespace ingress-default \
  --set controller.extraArgs.enable-ssl-passthrough=true \
  --set controller.service.externalTrafficPolicy=Local \
  --set controller.config.proxy-body-size=20m \
  ${ARGS[*]} \
  ${PROJECT_NAME}-in ingress-nginx/ingress-nginx
