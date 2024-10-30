#!/usr/bin/env bash

source $(dirname ${BASH_SOURCE[0]})/setenv.sh

check_docker
check_helm

COMPOSE_CONFIG=$(mktemp -d)/build.yml

cd ${PROJECT_PATH}/${WEB_MODULE}

rm -rf dist
npm run build;ret=$?
test ${ret} -eq 0 || die "ERROR: Failed to build ${WEB_MODULE}!"

web_version=$(awk -F'"' '/"version":/ {print $4}' package.json)
if [[ -n "${web_version}" ]]; then
  cp -f dist/assets/index.js dist/assets/index-${web_version}.js
fi

rm -rf ${PROJECT_PATH}/${STARTER_MODULE}/src/main/resources/static/assets
rm -rf ${PROJECT_PATH}/${STARTER_MODULE}/src/main/resources/static/img
cp -R -f ${PROJECT_PATH}/${WEB_MODULE}/dist/assets ${PROJECT_PATH}/${STARTER_MODULE}/src/main/resources/static/assets
cp -R -f ${PROJECT_PATH}/${WEB_MODULE}/dist/img ${PROJECT_PATH}/${STARTER_MODULE}/src/main/resources/static/img
cp -R -f ${PROJECT_PATH}/img/* ${PROJECT_PATH}/${STARTER_MODULE}/src/main/resources/static/img/

cd ${SCRIPTS_PATH}

mvn clean package -Dmaven.test.skip=true -f ${PROJECT_PATH}/pom.xml;ret=$?
test ${ret} -eq 0 || die "ERROR: Failed to build ${PROJECT_NAME}!"

cp -f ${PROJECT_PATH}/${STARTER_MODULE}/target/${STARTER_MODULE}-${VERSION}.jar ${DOCKER_CONFIG_HOME}/${PROJECT_NAME}.jar

# compose config
cat > ${COMPOSE_CONFIG} <<EOF
services:
  ${PROJECT_NAME}:
    build:
      context: ${DOCKER_CONFIG_HOME}
      dockerfile: Dockerfile
      args:
        - APP_NAME=${PROJECT_NAME}
        - UNAME=${HELM_backend_systemAccount:-admin}
        - UID=${HELM_backend_securityContext_runAsUser:-2024}
        - GID=${HELM_backend_securityContext_runAsGroup:-2024}
EOF

if [[ " ${ARGS[*]} " =~ " --release " ]]; then
  builder=$(docker buildx ls | grep "^multiple-platforms-builder" | awk '{print $1}')
  if [[ -z "${builder}" ]]; then
    docker buildx create --name multiple-platforms-builder --driver docker-container --bootstrap
  fi

  PUBLIC_REPO=freechatfun/freechat

  cat >> ${COMPOSE_CONFIG} <<EOF
      tags:
        - ${PUBLIC_REPO}:${VERSION}
      platforms:
        - linux/amd64
        - linux/arm64
    image: ${PUBLIC_REPO}:latest
EOF

  if [[ "${VERBOSE}" == "1" ]];then
    echo "[COMPOSE CONFIG]"
    cat ${COMPOSE_CONFIG}
  fi

  export DOCKER_BUILDKIT=1
  docker compose -f ${COMPOSE_CONFIG} -p ${PROJECT_NAME} build \
    --builder multiple-platforms-builder \
    --push \
    ${PROJECT_NAME}

else
  cat >> ${COMPOSE_CONFIG} <<EOF
      tags:
        - ${HELM_backend_image_repository}:${VERSION}
      platforms:
        - linux/amd64
    image: ${HELM_backend_image_repository}:latest
EOF

  if [[ "${VERBOSE}" == "1" ]];then
    echo "[COMPOSE CONFIG]"
    cat ${COMPOSE_CONFIG}
  fi

  docker compose -f ${COMPOSE_CONFIG} -p ${PROJECT_NAME} build --push ${PROJECT_NAME}
  helm dependency update ${HELM_CONFIG_HOME}
fi

rm -f ${DOCKER_CONFIG_HOME}/${PROJECT_NAME}.jar
