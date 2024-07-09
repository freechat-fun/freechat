#!/usr/bin/env bash

source $(dirname ${BASH_SOURCE[0]})/setenv.sh

check_kubectl

kubectl exec --kubeconfig ${KUBE_CONFIG} --namespace ${NAMESPACE} \
  -it ${PROJECT_NAME}-mysql-0 -- mysql -hlocalhost -uroot \
  -p${HELM_mysql_auth_rootPassword} -D${HELM_mysql_auth_database}