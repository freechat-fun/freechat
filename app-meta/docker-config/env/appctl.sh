#!/usr/bin/env bash

# chkconfig:   2345 90 10

PROG_NAME=$0
ACTION=${1:-restart}

if [[ -z "${JAR_PATH}" ]]; then
    JAR_FILE=${APP_NAME}.jar
    JAR_PATH=${APP_HOME}/${APP_NAME}.jar
fi

SERVICE_NAME=${APP_NAME}

if [[ -z "${SERVICE_OUT}" ]]; then
    SERVICE_OUT=$APP_HOME/logs/service_stdout.log
fi

if [[ -z "${STATUSROOT_HOME}" ]]; then
    STATUSROOT_HOME="${APP_HOME}/target/${APP_NAME}/META-INF/resources"
fi

usage() {
    echo "Usage: ${PROG_NAME} {start|stop|restart|status}"
    exit 2 # bad usage
}

do_start() {
    if [[ -z "${SERVER_PORT}" ]]; then
        SERVER_PORT=8080
    fi

    MANAGEMENT_PORT=$((SERVER_PORT+1))

    if [[ "${JPDA_ENABLE}" == "true" ]]; then
        if [[ -z "${JPDA_PORT}" ]]; then
            JPDA_PORT=5005
        fi
        JVM_OPTS="${JVM_OPTS} -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=${JPDA_PORT}"
    fi

    SERVICE_OPTS="${SERVICE_OPTS} -server"

    local memTotal=`cat /proc/meminfo | grep MemTotal | awk '{printf "%d", $2/1024 }'`
    echo "INFO: OS total memory: "${memTotal}"M"

    SERVICE_OPTS="${SERVICE_OPTS} -Djava.awt.headless=true"
    SERVICE_OPTS="${SERVICE_OPTS} -Dproject.name=${APP_NAME}"

    SERVER_OPTS="${SERVER_OPTS} --server.tomcat.uri-encoding=ISO-8859-1 --server.tomcat.max-threads=400"
    SERVER_OPTS="${SERVER_OPTS} --server.port=${SERVER_PORT}  -Dmanagement.port=${MANAGEMENT_PORT} -Dmanagement.server.port=${MANAGEMENT_PORT}"

    echo java ${JVM_OPTS} -jar ${SERVICE_OPTS} ${JAR_PATH} ${SERVER_OPTS} \>\>${SERVICE_OUT}
    if [[ "x${TEE_JAVA_LOG}" == "x1" ]]; then
        eval exec "java" ${JVM_OPTS} "-jar" ${SERVICE_OPTS} ${JAR_PATH} ${SERVER_OPTS} 2>&1 "|" "tee" "-a" ${SERVICE_OUT} "&"
    else
        eval exec "java" ${JVM_OPTS} "-jar" ${SERVICE_OPTS} ${JAR_PATH} ${SERVER_OPTS} &>>${SERVICE_OUT} "&"
    fi
}

check_start() {
    local exptime=0
    local time=300
    while true
    do
        ret=`(ss -ln4 sport = :${SERVER_PORT}; ss -ln6 sport = :${SERVER_PORT}) | grep -c ":${SERVER_PORT}"`
        if [ $ret -eq 0 ]; then
            sleep 1
            ((exptime++))
            if [ ${exptime} -gt ${time} ]; then
                echo "Wait Application Start timeout, exit 1."
                exit 1
            else
                echo "Wait Application Start: ${exptime}..."
            fi

            if [ `expr $exptime \% 10` -eq 0 ]; then
                local pid=$(get_pid)
                if [[ -z "${pid}" ]]; then
                    echo
                    echo "Application appears exit, start failed."
                    exit 4
                fi
            fi
        fi
    done
}

get_pid() {
    # ls -l /proc/[0-9]*/exe | grep "java" | awk -F/ '{print $3}'
    ps -ef | grep "java" | grep "project.name=${APP_NAME}" | awk '{print $2}'
}

start() {
    echo "INFO: ${APP_NAME} try to start..."
    do_start
    check_start
}

stop() {
    echo "INFO: ${APP_NAME} try to stop..."
    local pid=$(get_pid)
    if [[ -n "${pid}" ]]; then
        kill -15 ${pid}
        sleep 1
        local s=0
        pid=$(get_pid)
        while [[ -n "${pid}" && ${s} -le 60 ]]; do
            let s+=1
            sleep 1
            echo "INFO: Waiting for ${s} s..."
            pid=$(get_pid)
        done
    fi

    if [[ -n "${pid}" ]]; then
        echo "INFO: ${APP_NAME} stop failed! pid is ${pid}"
        exit -1
    fi

    echo "INFO: ${APP_NAME} stop successfully"
}

status() {
    local pid=$(get_pid)
    if [[ -n "${pid}" ]]; then
        echo "running"
    else
        echo "shutdown"
        exit 2
    fi
}

main() {
    case "${ACTION}" in
        start)
            start
        ;;
        stop)
            stop
        ;;
        restart)
            stop
            start
        ;;
        status)
            status
        ;;
        *)
            usage
        ;;
    esac
}

main
