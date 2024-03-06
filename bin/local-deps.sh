#!/usr/bin/env bash

source $(dirname ${BASH_SOURCE[0]})/setenv.sh

MODULE_PATH=${PROJECT_PATH}/${PROJECT_NAME}-dal
RESOURCE_PATH=${MODULE_PATH}/src/main/resources
SERVICES_NAME="mysql redis"
SERVICES_CONFIG=local-deps.yml

export MYSQL_VER=latest
export MYSQL_PASSWORD=hello1234
export MYSQL_NAME=${PROJECT_NAME}-mysql
export MYSQL_PORT=3306
export MYSQL_HOST_PORT=3307
export MYSQL_DATA=$(mktemp -d)

export REDIS_VER=latest
export REDIS_PASSWORD=hello1234
export REDIS_NAME=${PROJECT_NAME}-redis
export REDIS_PORT=6379
export REDIS_HOST_PORT=6380


if [[ " ${ARGS[*]} " =~ " --start " ]]; then
  mkdir -p ${MYSQL_DATA}/conf.d
  echo -e "[client]\ndefault_character_set=utf8\n[mysqld]\ncollation_server=utf8mb4_unicode_ci\ncharacter_set_server=utf8mb4\n" > ${MYSQL_DATA}/conf.d/my.conf

  mkdir -p ${MYSQL_DATA}/docker-entrypoint-initdb.d
  cp -f ${RESOURCE_PATH}/sql/schema.sql ${MYSQL_DATA}/docker-entrypoint-initdb.d/

  docker compose -f ${SERVICES_CONFIG} -p ${PROJECT_NAME} up --wait ${SERVICES_NAME}
elif [[ " ${ARGS[*]} " =~ " --stop " ]]; then
  docker compose -f ${SERVICES_CONFIG} -p ${PROJECT_NAME} down
  rm -rf ${MYSQL_DATA}
else
  docker compose -f ${SERVICES_CONFIG} -p ${PROJECT_NAME} top
fi
