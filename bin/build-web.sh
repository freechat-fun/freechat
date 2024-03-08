#!/usr/bin/env bash

source $(dirname ${BASH_SOURCE[0]})/setenv.sh

cd ${PROJECT_PATH}/${WEB_MODULE}
rm -rf dist
npm run build;ret=$?
if [[ ${ret} -ne 0 ]]; then
  exit 1
fi

web_version=$(awk -F'"' '/"version":/ {print $4}' package.json)
if [[ -n "${web_version}" ]]; then
    cp -f dist/assets/index.js dist/assets/index-${web_version}.js
fi

cd ${DOCKER_CONFIG_HOME}
rm -rf web
cp -R -f ${PROJECT_PATH}/${WEB_MODULE}/dist web

rm -rf ${PROJECT_PATH}/${STARTER_MODULE}/src/main/resources/static/assets
rm -rf ${PROJECT_PATH}/${STARTER_MODULE}/src/main/resources/static/img
cp -R -f ${PROJECT_PATH}/${WEB_MODULE}/dist/assets ${PROJECT_PATH}/${STARTER_MODULE}/src/main/resources/static/assets
cp -R -f ${PROJECT_PATH}/${WEB_MODULE}/dist/img ${PROJECT_PATH}/${STARTER_MODULE}/src/main/resources/static/img


iid=$(docker build --platform=linux/amd64 \
  --build-arg APP_NAME=${WEB_MODULE} \
  ${ARGS[*]} \
  -t ${HELM_image_frontend_repository}:latest \
  -f Dockerfile_web . || exit -1)

if [[ -n "${iid} " ]]; then
  docker push ${HELM_image_frontend_repository}:latest
fi

rm -rf web
