#!/usr/bin/env bash

source $(dirname ${BASH_SOURCE[0]})/setenv.sh

set -e

bash ./install-dind.sh

kubectl exec --kubeconfig ${KUBE_CONFIG} --namespace ${NAMESPACE} \
  -it docker -- /bin/sh -euc "\
    apk update && \
    apk add --no-cache bash git nodejs npm openjdk25 maven && \
    docker login && \
    if [ ! -d "/${PROJECT_NAME}" ]; then git clone ${SCM_URL} /${PROJECT_NAME}; fi && \
    cd /${PROJECT_NAME}/scripts && \
    git reset --hard && \
    git pull --ff-only && \
    ./build.sh --release"

bash ./uninstall-dind.sh
