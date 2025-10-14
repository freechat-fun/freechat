#!/usr/bin/env bash

source $(dirname ${BASH_SOURCE[0]})/setenv.sh

check_docker

MODULE_PATH=${PROJECT_PATH}/${PROJECT_NAME}-dal
RESOURCE_PATH=${MODULE_PATH}/src/main/resources
SERVICE_NAMES="mysql redis milvus"
SERVICES_CONFIG=local-deps.yml

export MYSQL_TAG=9.4.0
export MYSQL_PASSWORD=hello1234
export MYSQL_NAME=${PROJECT_NAME}-mysql
export MYSQL_PORT=3306
export MYSQL_HOST_PORT=3306
export MYSQL_VOLUME=${PROJECT_PATH}/local-data/mysql

export REDIS_TAG=8.2.2
export REDIS_PASSWORD=hello1234
export REDIS_NAME=${PROJECT_NAME}-redis
export REDIS_PORT=6379
export REDIS_HOST_PORT=6379
export REDIS_VOLUME=${PROJECT_PATH}/local-data/redis

export ETCD_TAG=v3.5.16
export ETCD_NAME=${PROJECT_NAME}-etcd
export ETCD_VOLUME=${PROJECT_PATH}/local-data/etcd
export ETCD_PORT=2379
export ETCD_HOST_PORT=2379

export MINIO_TAG=RELEASE.2025-09-07T16-13-09Z
export MINIO_NAME=${PROJECT_NAME}-minio
export MINIO_VOLUME=${PROJECT_PATH}/local-data/minio
export MINIO_PORT=9000
export MINIO_HOST_PORT=9000
export MINIO_CONTROL_PORT=9001
export MINIO_CONTROL_HOST_PORT=9001

export MILVUS_TAG=v2.5.19
export MILVUS_USERNAME=root
export MILVUS_PASSWORD=hello1234
export MILVUS_NAME=${PROJECT_NAME}-milvus
export MILVUS_PORT=19530
export MILVUS_HOST_PORT=19530
export MILVUS_CONTROL_PORT=9091
export MILVUS_CONTROL_HOST_PORT=9091
export MILVUS_VOLUME=${PROJECT_PATH}/local-data/milvus

export TTS_TAG=cpu-latest
export TTS_NAME=${PROJECT_NAME}-tts
export TTS_PORT=5002
export TTS_HOST_PORT=5002
export TTS_VOLUME=${PROJECT_PATH}/local-data/tts

if [[ " ${ARGS[*]} " =~ " --enable-tts " ]]; then
  SERVICE_NAMES="tts ${SERVICE_NAMES}"
fi

if [[ " ${ARGS[*]} " =~ " --start " ]]; then
  # config mysql
  mkdir -p ${MYSQL_VOLUME}/conf.d
  cat << EOF > ${MYSQL_VOLUME}/conf.d/my.conf
[client]
default_character_set=utf8
[mysqld]
collation_server=utf8mb4_unicode_ci
character_set_server=utf8mb4
EOF

  mkdir -p ${MYSQL_VOLUME}/docker-entrypoint-initdb.d
  cp -f ${RESOURCE_PATH}/sql/schema.sql ${MYSQL_VOLUME}/docker-entrypoint-initdb.d/initdb-1.sql
  cp -f ${RESOURCE_PATH}/sql/data-local.sql ${MYSQL_VOLUME}/docker-entrypoint-initdb.d/initdb-2.sql

  docker compose -f ${SERVICES_CONFIG} -p ${PROJECT_NAME} up --wait ${SERVICE_NAMES}
elif [[ " ${ARGS[*]} " =~ " --stop " ]]; then
  docker compose -f ${SERVICES_CONFIG} -p ${PROJECT_NAME} down
else
  docker compose -f ${SERVICES_CONFIG} -p ${PROJECT_NAME} top
fi
