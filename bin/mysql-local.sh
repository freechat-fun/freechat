#!/bin/bash

source $(dirname ${BASH_SOURCE[0]})/setenv.sh

MODULE_PATH=${PROJECT_PATH}/${PROJECT_NAME}-dal
RESOURCE_PATH=${MODULE_PATH}/src/main/resources
MYSQL_VER=8.0.33
MYSQL_PASSWORD=hello1234
MYSQL_NAME=${PROJECT_NAME}-mysql
MYSQL_DATA=$(mktemp -d)

function cleanup {
  docker stop $1 >/dev/null
  sleep 5
  docker rm -f $1 >/dev/null
}

function get_container_health {
  docker inspect --format "{{.State.Status}}" $1
}

function get_container_id {
  docker ps --filter "name=$1" --format "{{.ID}}"
}

function wait_container {
  while STATUS=$(get_container_health $1); [ $STATUS != "running" ]; do
    if [ $STATUS == "exited" -o $STATUS == "dead" ]; then
      echo "Failed!!!"
      cleanup $1
      exit -1
    fi
    printf .
    lf=$'\n'
    sleep 1
  done
  printf "$lf"
  sleep 30
}

if [[ " ${ARGS[*]} " =~ " --start " ]]; then
  mkdir -p ${MYSQL_DATA}/conf.d
  echo -e "[client]\ndefault_character_set=utf8\n[mysqld]\ncollation_server=utf8mb4_unicode_ci\ncharacter_set_server=utf8mb4\n" > ${MYSQL_DATA}/conf.d/my.conf

  mkdir -p ${MYSQL_DATA}/docker-entrypoint-initdb.d
  cp -f ${RESOURCE_PATH}/sql/schema.sql ${MYSQL_DATA}/docker-entrypoint-initdb.d/

  docker pull mysql:${MYSQL_VER}
  cid=$(docker run --name ${MYSQL_NAME} -d -p 3307:3306 \
    -v ${MYSQL_DATA}/conf.d:/etc/mysql/conf.d \
    -v ${MYSQL_DATA}/docker-entrypoint-initdb.d:/docker-entrypoint-initdb.d \
    -e MYSQL_ROOT_PASSWORD=${MYSQL_PASSWORD} \
    --health-cmd='mysqladmin ping --silent' \
    mysql:${MYSQL_VER} || exit -1)

  if [[ -z "${cid}" ]]; then
    echo "Create MySQL failed!"
    exit -2
  fi
  wait_container ${cid}

elif [[ " ${ARGS[*]} " =~ " --stop " ]]; then
  cid=$(get_container_id ${MYSQL_NAME})
  if [[ -z "${cid}" ]]; then
    echo "Failed to find ${MYSQL_NAME}!"
    exit -3
  fi
  cleanup ${cid}
  rm -rf ${MYSQL_DATA}

else
  cid=$(get_container_id ${MYSQL_NAME})
  if [[ -z "${cid}" ]]; then
    echo "Failed to find ${MYSQL_NAME}!"
    exit -3
  fi
  echo "MySQL is running in container ${cid}"
fi
