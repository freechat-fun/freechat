#!/usr/bin/env bash

source $(dirname ${BASH_SOURCE[0]})/setenv.sh

check_docker

MODULE_PATH=${PROJECT_PATH}/${PROJECT_NAME}-dal
RESOURCE_PATH=${MODULE_PATH}/src/main/resources
SERVICE_NAMES="mysql redis milvus"
SERVICES_CONFIG=local-deps.yml

export MYSQL_TAG=latest
export MYSQL_PASSWORD=hello1234
export MYSQL_NAME=${PROJECT_NAME}-mysql
export MYSQL_PORT=3306
export MYSQL_HOST_PORT=3306
export MYSQL_VOLUME=${PROJECT_PATH}/local-data/mysql

export REDIS_TAG=latest
export REDIS_PASSWORD=hello1234
export REDIS_NAME=${PROJECT_NAME}-redis
export REDIS_PORT=6379
export REDIS_HOST_PORT=6379
export REDIS_VOLUME=${PROJECT_PATH}/local-data/redis

export MILVUS_TAG=latest
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
  cp -f ${RESOURCE_PATH}/sql/data.sql ${MYSQL_VOLUME}/docker-entrypoint-initdb.d/initdb-2.sql
  cp -f ${RESOURCE_PATH}/sql/data-local.sql ${MYSQL_VOLUME}/docker-entrypoint-initdb.d/initdb-3.sql

  # config milvus
  mkdir -p ${MILVUS_VOLUME}/configs
  cat << EOF > ${MILVUS_VOLUME}/configs/embedEtcd.yaml
listen-client-urls: http://0.0.0.0:2379
advertise-client-urls: http://0.0.0.0:2379
quota-backend-bytes: 4294967296
auto-compaction-mode: revision
auto-compaction-retention: '1000'
EOF

  docker-compose -f ${SERVICES_CONFIG} -p ${PROJECT_NAME} up --wait ${SERVICE_NAMES}
elif [[ " ${ARGS[*]} " =~ " --stop " ]]; then
  docker-compose -f ${SERVICES_CONFIG} -p ${PROJECT_NAME} down
else
  docker-compose -f ${SERVICES_CONFIG} -p ${PROJECT_NAME} top
fi
