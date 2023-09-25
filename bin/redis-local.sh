#!/bin/bash

source $(dirname ${BASH_SOURCE[0]})/setenv.sh

REDIS_REPO=redis
REDIS_VER=latest
REDIS_PASSWORD=hello1234
REDIS_NAME=${PROJECT_NAME}-redis

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
  docker pull ${REDIS_REPO}:${REDIS_VER}
  cid=$(docker run \
    -e REDIS_PASSWORD=${REDIS_PASSWORD} \
    --name ${REDIS_NAME} \
    -p 6379:6379 \
    -d ${REDIS_REPO}:${REDIS_VER} --appendonly yes  --requirepass ${REDIS_PASSWORD} || exit -1)

  if [[ -z "${cid}" ]]; then
    echo "Create ${REDIS_NAME} failed!"
    exit -2
  fi
  wait_container ${cid}
elif [[ " ${ARGS[*]} " =~ " --stop " ]]; then
  cid=$(get_container_id ${REDIS_NAME})
  if [[ -n "${cid}" ]]; then
    cleanup ${cid}
  else
    echo "Failed to find ${REDIS_NAME}!"
  fi
else
  cid=$(get_container_id ${REDIS_NAME})
  if [[ -n "${cid}" ]]; then
    echo "${REDIS_NAME} is running in container ${cid}"
  else
    echo "Failed to find ${REDIS_NAME}!"
  fi
fi
