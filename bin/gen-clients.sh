#!/bin/bash

source $(dirname ${BASH_SOURCE[0]})/setenv.sh

# Usage: https://openapi-generator.tech/docs/usage

GIT_HOST=github.com
GIT_USER_ID=freechat-fun
GIT_REPO_ID=freechat
GROUP_ID=$(sed -n "s#^ *<groupId>\([a-zA-Z0-9.-]\{1,\}\)</groupId> *\$#\1#p" ${PROJECT_PATH}/pom.xml | head -1)
ARTIFACT_ID=$(sed -n "s#^ *<artifactId>\([a-zA-Z0-9.-]\{1,\}\)</artifactId> *\$#\1#p" ${PROJECT_PATH}/pom.xml | head -1)-sdk
PACKAGE=${GROUP_ID}
ARTIFACT_URL=https://freechat.fun/public/docs/api
SCM_CONNECTION=scm:git:git@${GIT_HOST}:${GIT_USER_ID}/${GIT_REPO_ID}.git
SCM_URL=https://${GIT_HOST}/${GIT_USER_ID}/${GIT_REPO_ID}
LICENSE_NAME=Apache-2.0
LICENSE_URL=https://www.apache.org/licenses/LICENSE-2.0
AUTHOR_NAME=dev.freechat.fun
AUTHOR_EMAIL=dev.freechat.fun@gmail.com
AUTHOR_URL=https://github.com/freechat-fun
AUTHOR_ORG=freechat.fun
OUTPUT=${PROJECT_PATH}/local-data/sdk
CLI=${OUTPUT}/openapi-generator-cli.jar
CLI_URL=https://repo1.maven.org/maven2/org/openapitools/openapi-generator-cli/7.1.0/openapi-generator-cli-7.1.0.jar
DOC=http://127.0.0.1:8080/public/openapi/v3/api-docs/g-all

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
developerEmail=${AUTHOR_EMAIL},\
developerName=${AUTHOR_NAME},\
developerOrganization=${AUTHOR_ORG},\
developerOrganizationUrl=${AUTHOR_URL},\
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

  java --add-opens java.base/java.lang=ALL-UNNAMED \
    --add-opens java.base/java.util=ALL-UNNAMED \
    -jar ${CLI} generate \
    -i ${DOC} \
    -g python \
    -o ${output} \
    --artifact-id ${ARTIFACT_ID} \
    --artifact-version ${VERSION} \
    --git-host ${GIT_HOST} \
    --git-repo-id ${GIT_REPO_ID} \
    --git-user-id ${GIT_USER_ID} \
    --group-id ${GROUP_ID} \
    --package-name ${ARTIFACT_ID} \
    --http-user-agent ${artifact_id}/${VERSION}/python \
    --additional-properties \
disallowAdditionalPropertiesIfNotPresent=false,\
enumUnknownDefaultCase=true,\
hideGenerationTimestamp=true,\
packageName=${ARTIFACT_ID},\
packageVersion=${VERSION},\
projectName=${ARTIFACT_ID}
}

function javascript_sdk {
  local output=${OUTPUT}/javascript
  rm -rf ${output}
  mkdir -p ${output}

  java --add-opens java.base/java.lang=ALL-UNNAMED \
    --add-opens java.base/java.util=ALL-UNNAMED \
    -jar ${CLI} generate \
    -i ${DOC} \
    -g javascript \
    -o ${output} \
    --artifact-id ${ARTIFACT_ID} \
    --artifact-version ${VERSION} \
    --git-host ${GIT_HOST} \
    --git-repo-id ${GIT_REPO_ID} \
    --git-user-id ${GIT_USER_ID} \
    --group-id ${GROUP_ID} \
    --package-name ${ARTIFACT_ID} \
    --http-user-agent ${artifact_id}/${VERSION}/python \
    --additional-properties \
disallowAdditionalPropertiesIfNotPresent=false,\
enumUnknownDefaultCase=true,\
hideGenerationTimestamp=true,\
licenseName="${LICENSE_NAME}",\
licenseUrl=${LICENSE_URL},\
moduleName=${ARTIFACT_ID},\
packageName=${ARTIFACT_ID},\
packageVersion=${VERSION},\
projectName=${ARTIFACT_ID},\
usePromises=true
}

if [[ " ${ARGS[*]} " =~ " --java " ]]; then
  java_sdk
elif [[ " ${ARGS[*]} " =~ " --python " ]]; then
  python_sdk
elif [[ " ${ARGS[*]} " =~ " --javascript " ]]; then
  javascript_sdk
else
  java_sdk
  python_sdk
  javascript_sdk
fi
