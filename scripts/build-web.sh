#!/usr/bin/env bash

source $(dirname "${BASH_SOURCE[0]}")/setenv.sh

check_docker

COMPOSE_CONFIG=$(mktemp -d)/build-web.yml

cd ${PROJECT_PATH}/"${WEB_MODULE}" || exit
rm -rf dist
npm run build;ret=$?
test ${ret} -eq 0 || die "ERROR: Failed to build ${WEB_MODULE}!"

web_version=$(awk -F'"' '/"version":/ {print $4}' package.json)
if [[ -n "${web_version}" ]]; then
  cp -f dist/assets/index.js dist/assets/index-${web_version}.js
fi

cd "${DOCKER_CONFIG_HOME}" || exit
rm -rf web
cp -R -f ${PROJECT_PATH}/${WEB_MODULE}/dist web

rm -rf ${PROJECT_PATH}/${STARTER_MODULE}/src/main/resources/static/assets
rm -rf ${PROJECT_PATH}/${STARTER_MODULE}/src/main/resources/static/img
cp -R -f ${PROJECT_PATH}/${WEB_MODULE}/dist/assets ${PROJECT_PATH}/${STARTER_MODULE}/src/main/resources/static/assets
cp -R -f ${PROJECT_PATH}/${WEB_MODULE}/dist/img ${PROJECT_PATH}/${STARTER_MODULE}/src/main/resources/static/img
cp -R -f ${PROJECT_PATH}/img/* ${PROJECT_PATH}/${STARTER_MODULE}/src/main/resources/static/img/

# compose config
cat > ${COMPOSE_CONFIG} <<EOF
services:
  ${WEB_MODULE}:
    build:
      context: ${DOCKER_CONFIG_HOME}
      dockerfile: Dockerfile_web
      args:
        - APP_NAME=${WEB_MODULE}
      tags:
        - ${HELM_frontend_image_repository}:${VERSION}
      platforms:
        - linux/amd64
    image: ${HELM_frontend_image_repository}:latest
EOF

if [[ "${VERBOSE}" == "1" ]];then
  echo "[COMPOSE CONFIG]"
  cat ${COMPOSE_CONFIG}
fi

if [[ " ${ARGS[*]} " =~ " --push " ]]; then
  docker-compose -f ${COMPOSE_CONFIG} -p ${WEB_MODULE} build --push ${WEB_MODULE}
else
  docker-compose -f ${COMPOSE_CONFIG} -p ${WEB_MODULE} build ${WEB_MODULE}
fi

rm -rf web
