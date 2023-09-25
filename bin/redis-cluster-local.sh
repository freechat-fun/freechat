#!/bin/bash

source $(dirname ${BASH_SOURCE[0]})/setenv.sh

REDIS_REPO=bitnami/redis-cluster
REDIS_VER=latest
REDIS_PASSWORD=${LOCAL_redis_datasource_password:-hello1234}
REDIS_NAME=${PROJECT_NAME}-redis-cluster
REDIS_NETWORK=${REDIS_NAME}-net

function cleanup {
  docker stop $1 >/dev/null
  sleep 5
  docker rm -f $1 >/dev/null
}

function create_network_if_necessary {
  local net=$(docker network ls | grep ${REDIS_NETWORK})
  if [[ -z "${net}" ]]; then
    docker network create ${REDIS_NETWORK}
  fi
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
  docker pull ${REDIS_REPO}:${REDIS_VER}
  create_network_if_necessary
  for i in {0..5}
  do
    cid=$(docker run \
      -e REDIS_PASSWORD=${REDIS_PASSWORD} \
      -e REDIS_NODES=${REDIS_NAME}-0,${REDIS_NAME}-1,${REDIS_NAME}-2,${REDIS_NAME}-3,${REDIS_NAME}-4,${REDIS_NAME}-5 \
      --network ${REDIS_NETWORK} \
      --name ${REDIS_NAME}-$i \
      -p $((i+6379)):6379 \
      -d ${REDIS_REPO}:${REDIS_VER} || exit -1)

    if [[ -z "${cid}" ]]; then
      echo "Create ${REDIS_NAME}-$i failed!"
      exit -2
    fi
    wait_container ${cid}
  done
  docker exec -it ${REDIS_NAME}-0 redis-cli -a ${REDIS_PASSWORD} --cluster \
    create ${REDIS_NAME}-0:6379 ${REDIS_NAME}-1:6379 ${REDIS_NAME}-2:6379 ${REDIS_NAME}-3:6379 ${REDIS_NAME}-4:6379 ${REDIS_NAME}-5:6379 \
    --cluster-replicas 1 --cluster-yes
elif [[ " ${ARGS[*]} " =~ " --stop " ]]; then
  for i in {0..5}
  do
    cid=$(get_container_id ${REDIS_NAME}-$i)
    if [[ -n "${cid}" ]]; then
      cleanup ${cid}
    else
      echo "Failed to find ${REDIS_NAME}-$i!"
    fi
  done
else
  for i in {0..5}
  do
    cid=$(get_container_id ${REDIS_NAME}-$i)
    if [[ -n "${cid}" ]]; then
      echo "${REDIS_NAME}-$i is running in container ${cid}"
    else
      echo "Failed to find ${REDIS_NAME}-$i!"
    fi
  done
fi
