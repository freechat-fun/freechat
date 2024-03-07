FROM openjdk:21-bullseye

ARG APP_NAME=freechat
ARG UNAME=admin
ARG UID=2023
ARG GID=2023

ENV APP_NAME=${APP_NAME}
ENV APP_HOME=/home/${UNAME}/${APP_NAME}

RUN groupadd -g ${GID} -o ${UNAME}
RUN useradd -m -u ${UID} -g ${GID} -o ${UNAME}

COPY env/* ${APP_HOME}/

RUN apt-get update && \
    apt-get install -y iproute2 && \
    mkdir ${APP_HOME}/logs && \
    chmod +x ${APP_HOME}/*.sh && \
    chown -R ${UNAME}:${UNAME} ${APP_HOME}

COPY ${APP_NAME}.jar ${APP_HOME}/${APP_NAME}.jar

USER ${UNAME}

WORKDIR ${APP_HOME}

ENTRYPOINT ["/bin/bash", "entrypoint.sh"]
