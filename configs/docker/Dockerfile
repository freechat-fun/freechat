FROM openjdk:21-bookworm

ARG APP_NAME
ARG UNAME=admin
ARG UID=10000
ARG GID=10000

ENV APP_NAME=${APP_NAME}
ENV APP_HOME=/home/${UNAME}/${APP_NAME}

RUN groupadd -g ${GID} -o ${UNAME}
RUN useradd -m -u ${UID} -g ${GID} -o ${UNAME}

COPY scripts/* ${APP_HOME}/

RUN chmod +x ${APP_HOME}/*.sh && \
    chown -R ${UNAME}:${UNAME} ${APP_HOME}

COPY ${APP_NAME}.jar ${APP_HOME}/${APP_NAME}.jar

USER ${UNAME}

WORKDIR ${APP_HOME}

ENTRYPOINT ["/bin/bash", "entrypoint.sh"]
