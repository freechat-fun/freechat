#!/bin/bash

source $(dirname ${BASH_SOURCE[0]})/setenv.sh

# Usage: https://openapi-generator.tech/docs/usage

GIT_HOST=github.com
GIT_USER_ID=freechat-fun
GIT_REPO_ID=freechat-java
GROUP_ID=$(sed -n "s#^ *<groupId>\([a-zA-Z0-9.-]\{1,\}\)</groupId> *\$#\1#p" ${PROJECT_PATH}/pom.xml | head -1)
ARTIFACT_ID=freechat-java
PACKAGE=${GROUP_ID}
ARTIFACT_URL=https://freechat.fun/public/docs/api
SCM_CONNECTION=scm:git:git@${GIT_HOST}:${GIT_USER_ID}/${GIT_REPO_ID}.git
SCM_URL=https://${GIT_HOST}/${GIT_USER_ID}/${GIT_REPO_ID}
LICENSE_NAME=Apache-2.0
LICENSE_URL=https://www.apache.org/licenses/LICENSE-2.0
OUTPUT=${PROJECT_PATH}/local-data/sdk
CLI=${OUTPUT}/openapi-generator-cli.jar
CLI_URL=https://repo1.maven.org/maven2/org/openapitools/openapi-generator-cli/7.0.1/openapi-generator-cli-7.0.1.jar
DOC=https://freechat.fun/public/openapi/v3/api-docs/g-all

while [ $# -gt 0 ]
do
  case $1 in
    --group-id)
      GROUP_ID=$2
      shift
      shift
      ;;
    --artifact-id)
      ARTIFACT_ID=$2
      shift
      shift
      ;;
    --version)
      VERSION=$2
      shift
      shift
      ;;
    --package)
      PACKAGE=$2
      shift
      shift
      ;;
    --scm-connection)
      SCM_CONNECTION=$2
      shift
      shift
      ;;
    --scm-url)
      SCM_URL=$2
      shift
      shift
      ;;
    --doc)
      DOC=$2
      shift
      shift
      ;;
    -o|--output)
      OUTPUT=$2
      shift
      shift
      ;;
    -v|--verbose)
      set -eux
      ARGS+=("-v")
      shift
      ;;
    *)
      ARGS+=("$1")
      shift
      ;;
  esac
done

if [[ ! -f "${CLI}" ]]; then
  curl -L -o ${CLI} ${CLI_URL}
fi

function java_sdk {
  local output=${OUTPUT}/java
  rm -rf ${output}
  mkdir -p ${output}

  java --add-opens java.base/java.lang=ALL-UNNAMED \
    --add-opens java.base/java.util=ALL-UNNAMED \
    -jar ${CLI} generate \
    -i ${DOC} \
    -g java \
    -o ${output} \
    --api-package ${PACKAGE}.client.api \
    --artifact-id ${ARTIFACT_ID} \
    --artifact-version ${VERSION} \
    --git-host ${GIT_HOST} \
    --git-repo-id ${GIT_REPO_ID} \
    --git-user-id ${GIT_USER_ID} \
    --group-id ${GROUP_ID} \
    --invoker-package ${PACKAGE}.client \
    --package-name ${PACKAGE} \
    --http-user-agent ${ARTIFACT_ID}/${VERSION}/java \
    --additional-properties \
apiPackage=${PACKAGE}.client.api,\
artifactDescription=${ARTIFACT_ID},\
artifactId=${ARTIFACT_ID},\
artifactUrl=${ARTIFACT_URL},\
artifactVersion=${VERSION},\
booleanGetterPrefix=is,\
developerEmail=dev.freechat.fun@gmail.com,\
developerName=dev.freechat.fun,\
developerOrganization=freechat.fun,\
developerOrganizationUrl=https://github.com/freechat-fun,\
disallowAdditionalPropertiesIfNotPresent=false,\
enumUnknownDefaultCase=true,\
groupId=${GROUP_ID},\
hideGenerationTimestamp=true,\
invokerPackage=${PACKAGE}.client,\
licenseName="${LICENSE_NAME}",\
licenseUrl=${LICENSE_URL},\
modelPackage=${PACKAGE}.client.model,\
scmConnection=${SCM_CONNECTION},\
scmDeveloperConnection=${SCM_CONNECTION},\
scmUrl=${SCM_URL}
}

function python_sdk {
  local output=${OUTPUT}/python
  rm -rf ${output}
  mkdir -p ${output}

  repo_id=freechat-python
  artifact_id=freechat-python
  package=aihub

  java --add-opens java.base/java.lang=ALL-UNNAMED \
    --add-opens java.base/java.util=ALL-UNNAMED \
    -jar ${CLI} generate \
    -i ${DOC} \
    -g python \
    -o ${output} \
    --artifact-id ${artifact_id} \
    --artifact-version ${VERSION} \
    --git-host ${GIT_HOST} \
    --git-repo-id ${repo_id} \
    --git-user-id ${GIT_USER_ID} \
    --group-id ${GROUP_ID} \
    --package-name ${package} \
    --http-user-agent ${artifact_id}/${VERSION}/python \
    --additional-properties \
disallowAdditionalPropertiesIfNotPresent=false,\
enumUnknownDefaultCase=true,\
hideGenerationTimestamp=true,\
packageName=${package},\
packageVersion=${VERSION},\
projectName=${artifact_id}
}

function js_sdk {
  local output=${OUTPUT}/js
  rm -rf ${output}
  mkdir -p ${output}

  repo_id=freechat-js
  artifact_id=freechat-js
  package=aihub

  java --add-opens java.base/java.lang=ALL-UNNAMED \
    --add-opens java.base/java.util=ALL-UNNAMED \
    -jar ${CLI} generate \
    -i ${DOC} \
    -g javascript \
    -o ${output} \
    --artifact-id ${artifact_id} \
    --artifact-version ${VERSION} \
    --git-host ${GIT_HOST} \
    --git-repo-id ${repo_id} \
    --git-user-id ${GIT_USER_ID} \
    --group-id ${GROUP_ID} \
    --package-name ${package} \
    --http-user-agent ${artifact_id}/${VERSION}/python \
    --additional-properties \
disallowAdditionalPropertiesIfNotPresent=false,\
enumUnknownDefaultCase=true,\
hideGenerationTimestamp=true,\
licenseName="${LICENSE_NAME}",\
licenseUrl=${LICENSE_URL},\
moduleName=${package},\
packageName=${package},\
packageVersion=${VERSION},\
projectName=${artifact_id},\
usePromises=true
}

if [[ " ${ARGS[*]} " =~ " --java " ]]; then
  java_sdk
elif [[ " ${ARGS[*]} " =~ " --python " ]]; then
  python_sdk
elif [[ " ${ARGS[*]} " =~ " --js " ]]; then
  js_sdk
else
  java_sdk
  python_sdk
  js_sdk
fi
