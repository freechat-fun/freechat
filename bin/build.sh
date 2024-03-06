#!/usr/bin/env bash

source $(dirname ${BASH_SOURCE[0]})/setenv.sh

cd ${PROJECT_PATH}
mvn clean
mvn package -Dmaven.test.skip=true;ret=$?
if [[ ${ret} -ne 0 ]]; then
  exit 1
fi

cd ${DOCKER_CONFIG_HOME}

cp -f ${PROJECT_PATH}/${STARTER_MODULE}/target/${STARTER_MODULE}-${VERSION}.jar ${PROJECT_NAME}.jar

iid=$(docker build --platform=linux/amd64 \
  --build-arg APP_NAME=${PROJECT_NAME} \
  --build-arg UNAME=${HELM_systemAccount:-admin} \
  --build-arg UID=${HELM_securityContext_runAsUser:-2023} \
  --build-arg GID=${HELM_securityContext_runAsGroup:-2023} \
  ${ARGS[*]} \
  -t ${HELM_image_backend_repository}:latest \
  -f Dockerfile . || exit -1)

if [[ -n "${iid} " ]]; then
  docker push ${HELM_image_backend_repository}:latest
fi

rm -f ${PROJECT_NAME}.jar

cd ${HELM_CONFIG_HOME}
helm dependency build ${HELM_CONFIG_HOME}
