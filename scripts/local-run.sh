#!/usr/bin/env bash

source $(dirname ${BASH_SOURCE[0]})/setenv.sh

check_docker

set -- "${ARGS[@]}"

DEP_CONFIG=local-deps.yml
APP_CONFIG=local-app.yml
COMMAND=start
HOST_PORT=80
TAG=latest
APP_NAME=${PROJECT_NAME}
APP_ARGS=()
OTHER_ARGS=()

while [ $# -gt 0 ]
do
  case $1 in
    --start)
      COMMAND=start
      shift
      ;;
    --stop)
      COMMAND=stop
      shift
      ;;
    --update)
      COMMAND=update
      shift
      ;;
    --top)
      COMMAND=top
      shift
      ;;
    -p|--port)
      HOST_PORT=$2
      shift
      shift
      ;;
    -t|--tag)
      TAG=$2
      shift
      shift
      ;;
    -n|--name)
      APP_NAME=$2
      shift
      shift
      ;;
    -D*)
      APP_ARGS+=("$1")
      shift
      ;;
    --help)
      cat <<EOF
Run freechat.fun locally.

Options：
      --start (default) Start freechat.fun.
      --stop            Stop freechat.fun.
      --update          Update the image and restart freechat.fun.
      --top             Print the status of freechat.fun's containers.
  -n, --name            Service name, default is 'freechat'.
  -p, --port            Port number the service runs on, default is 80.
  -t, --tag             Tag of the image used by freechat.fun, default is latest.
      -D*               Override any application property.
                        E.g. '-Dapp.logging.level=debug' sets the logging level to debug.
                        Refer to the properties in freechat-start/src/main/resources/application.yml
      --help            Print usage.
EOF
      exit 0
      ;;
    *)
      OTHER_ARGS+=("$1")
      shift
      ;;
  esac
done

MODULE_PATH=${PROJECT_PATH}/${PROJECT_NAME}-dal
RESOURCE_PATH=${MODULE_PATH}/src/main/resources
SERVICE_NAMES="mysql redis milvus ${PROJECT_NAME}"

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

export REPOSITORY=freechatfun/freechat
export APP_VOLUME=${PROJECT_PATH}/local-data/${PROJECT_NAME}
export HOST_PORT
export TAG
export APP_NAME
export SERVICE_OPTS="\
  -Dspring.config.location=classpath:/application.yml,classpath:/application-local.yml \
  -Dspring.profiles.active=local \
  -Dembedding.milvus.url=\"http://milvus:${MILVUS_PORT}\" \
  -Dspring.datasource.url=\"jdbc:mysql://mysql:${MYSQL_PORT}/${PROJECT_NAME}?useUnicode=true&characterEncoding=utf-8\" \
  -Dredis.datasource.url=\"redis://redis:${REDIS_PORT}\" \
  ${APP_ARGS[@]}"

if [[ "${COMMAND}" == "start" ]]; then
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

  docker compose -f ${DEP_CONFIG} -f ${APP_CONFIG} -p ${PROJECT_NAME} up -d --wait ${SERVICE_NAMES}
elif [[ "${COMMAND}" == "stop" ]]; then
  docker compose -f ${DEP_CONFIG} -f ${APP_CONFIG} -p ${PROJECT_NAME} down
elif [[ "${COMMAND}" == "update" ]];then
  docker compose -f ${DEP_CONFIG} -f ${APP_CONFIG} -p ${PROJECT_NAME} pull ${PROJECT_NAME}
  docker compose -f ${DEP_CONFIG} -f ${APP_CONFIG} -p ${PROJECT_NAME} up -d --force-recreate --wait ${PROJECT_NAME}
else
  docker compose -f ${DEP_CONFIG} -f ${APP_CONFIG} -p ${PROJECT_NAME} top
fi
