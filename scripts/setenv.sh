#!/usr/bin/env bash

SCRIPTS_PATH=$(cd $(dirname ${BASH_SOURCE[0]}); pwd)
PROJECT_PATH=$(cd $(dirname ${BASH_SOURCE[0]})/..; pwd)
PROJECT_NAME=${PROJECT_PATH##*/}
VERSION=$(sed -n "s#^ *<revision>\([a-zA-Z0-9.-]\{1,\}\)</revision> *\$#\1#p" ${PROJECT_PATH}/pom.xml)

DAL_MODULE=${PROJECT_NAME}-dal
STARTER_MODULE=${PROJECT_NAME}-start
SDK_MODULE=${PROJECT_NAME}-sdk
WEB_MODULE=${PROJECT_NAME}-web
DOCKER_CONFIG_HOME=${PROJECT_PATH}/configs/docker
HELM_CONFIG_HOME=${PROJECT_PATH}/configs/helm

KUBE_CONFIG=${HELM_CONFIG_HOME}/kube-private.conf
HELM_CONFIG=${HELM_CONFIG_HOME}/values-private.yaml
NAMESPACE=
VERBOSE=0
ARGS=()

while [ $# -gt 0 ]
do
  case $1 in
    --kubeconfig)
      KUBE_CONFIG=$2
      shift
      shift
      ;;
    --helmconfig)
      HELM_CONFIG=$2
      shift
      shift
      ;;
    -n|--namespace)
      NAMESPACE=$2
      shift
      shift
      ;;
    -v|--verbose)
      set -eux
      VERBOSE=1
      shift
      ;;
    *)
      ARGS+=("$1")
      shift
      ;;
  esac
done

function parse_yaml {
  local prefix=$2
  local s='[[:space:]]*' w='[a-zA-Z0-9_]*' fs=$(echo @ | tr @ '\034')
  sed -ne "s|^\($s\):|\1|" \
      -e "s|^\($s\)\($w\)$s:$s[\"']\(.*\)[\"']$s\$|\1$fs\2$fs\3|p" \
      -e "s|^\($s\)\($w\)$s:$s\(.*\)$s\$|\1$fs\2$fs\3|p" $1 |
  awk -F$fs '{
    indent = length($1)/2;
    vname[indent] = $2;
    for (i in vname) {
      if (i > indent) {
        delete vname[i];
      }
    }
    if (length($3) > 0) {
      vn="";
      for (i = 0; i < indent; i++) {
        vn=(vn)(vname[i])("_");
      }
      printf("%s%s%s=\"%s\"\n", "'$prefix'",vn, $2, $3);
    }
  }'
}

eval $(parse_yaml "${HELM_CONFIG_HOME}/Chart.yaml" HELM_)
eval $(parse_yaml "${HELM_CONFIG_HOME}/values.yaml" HELM_)
values_yaml="${HELM_CONFIG_HOME}/values.yaml"
if [[ -f "${HELM_CONFIG}" ]]; then
  eval $(parse_yaml "${HELM_CONFIG}" HELM_)
  values_yaml="${HELM_CONFIG}"
fi

if [[ -f "${PROJECT_PATH}/${STARTER_MODULE}/src/main/resources/application-local.yml" ]]; then
  eval $(parse_yaml "${PROJECT_PATH}/${STARTER_MODULE}/src/main/resources/application-local.yml" LOCAL_)
fi

if [[ -z "${NAMESPACE}" ]]; then
  if [[ -n "${HELM_annotations_group}" ]]; then
    NAMESPACE=${HELM_annotations_group//./-}
  else
    NAMESPACE=default
  fi
fi

die () {
    echo "$*"
    echo
    exit 1
} >&2
