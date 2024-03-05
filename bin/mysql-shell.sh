#!/bin/bash

source $(dirname ${BASH_SOURCE[0]})/setenv.sh

kubectl exec --kubeconfig ${KUBE_CONFIG} --namespace ${NAMESPACE} \
  -it ${PROJECT_NAME}-mysql-0 -- mysql -hlocalhost -u${HELM_mysql_auth_username} \
  -p${HELM_mysql_auth_password} -D${HELM_mysql_auth_database}