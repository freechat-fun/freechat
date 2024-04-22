#!/usr/bin/env bash

source $(dirname ${BASH_SOURCE[0]})/setenv.sh

check_helm

helm --kubeconfig ${KUBE_CONFIG} --namespace ingress-default delete ${PROJECT_NAME}