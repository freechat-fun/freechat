#!/bin/bash

source $(dirname ${BASH_SOURCE[0]})/setenv.sh

MODULE_PATH=${PROJECT_PATH}/${PROJECT_NAME}-dal
RESOURCE_PATH=${MODULE_PATH}/src/main/resources

MYSQL_DRIVER=com.mysql.cj.jdbc.Driver
MYSQL_VER=8.0.33
MYSQL_URL="jdbc:mysql://127.0.0.1:3309?useUnicode=true&characterEncoding=utf-8"
MYSQL_USERNAME=root
MYSQL_PASSWORD=passwordformgb
MYSQL_NAME=mysql-for-mgb
MYSQL_DATA=$(mktemp -d)

function cleanup {
  docker stop $1 >/dev/null
  sleep 5
  docker rm -f $1 >/dev/null
}

function get_container_health {
  docker inspect --format "{{.State.Status}}" $1
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

mkdir -p ${MYSQL_DATA}/conf.d
echo -e "[client]\ndefault_character_set=utf8\n[mysqld]\ncollation_server=utf8mb4_unicode_ci\ncharacter_set_server=utf8mb4\n" > ${MYSQL_DATA}/conf.d/my.conf

docker pull mysql:${MYSQL_VER}
cid=$(docker run --name ${MYSQL_NAME} -d -p 3309:3306 \
  -v ${MYSQL_DATA}/conf.d:/etc/mysql/conf.d \
  -e MYSQL_ROOT_PASSWORD=${MYSQL_PASSWORD} \
  --health-cmd='mysqladmin ping --silent' \
  mysql:${MYSQL_VER} || exit -1)

if [[ -z "${cid}" ]]; then
    echo "Create MySQL failed!"
    exit -2
fi

wait_container ${cid}

cd ${MODULE_PATH}
mvn -f ${MODULE_PATH}/pom.xml \
  -Dmybatis.generator.jdbcDriver="${MYSQL_DRIVER}" \
  -Dmybatis.generator.jdbcUserId="${MYSQL_USERNAME}" \
  -Dmybatis.generator.jdbcPassword="${MYSQL_PASSWORD}" \
  -Dmybatis.generator.jdbcURL="${MYSQL_URL}" \
  -Dmybatis.generator.sqlScript=classpath:sql/schema.sql \
  -Dmybatis.generator.configurationFile=${RESOURCE_PATH}/mybatis-generator/generatorConfig.xml \
  -Dmybatis.generator.includeAllDependencies=true \
  -Dmybatis.generator.overwrite=true \
  mybatis-generator:generate;ret=$?

cleanup ${cid}

rm -rf ${MYSQL_DATA}

exit ${ret}
