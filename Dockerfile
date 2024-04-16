FROM openjdk:17-alpine

VOLUME /tmp

ADD platform-core/dpds-parser/target/odm-platform-core-dpds-parser-*.jar ./
ADD product-plane-services/registry-api/target/odm-platform-pp-registry-api-*.jar ./
ADD utility-plane-services/notification-api/target/odm-platform-up-notification-api-*.jar ./
COPY product-plane-services/registry-server/target/odm-platform-pp-registry-server-*.jar ./application.jar

ARG SPRING_PROFILES_ACTIVE=docker
ARG SPRING_PORT=8001
ARG SPRING_PROPS
ARG JAVA_OPTS
ARG DATABASE_URL=jdbc:postgresql://localhost:5432/odmpdb
ARG DATABASE_USERNAME=usr
ARG DATABASE_PASSWORD=pwd
ARG FLYWAY_SCHEMA=ODMREGISTRY
ARG FLYWAY_SCRIPTS_DIR=postgresql
ARG H2_CONSOLE_ENABLED=false
ARG H2_CONSOLE_PATH=h2-console
ARG METASERVICE_ACTIVE=false
ARG METASERVICE_HOSTNAME=localhost
ARG METASERVICE_PORT=9002
ARG POLICYSERVICE_ACTIVE=false
ARG POLICYSERVICE_HOSTNAME=localhost
ARG POLICYSERVICE_PORT=8005
ARG EVENTNOTIFIERSERVICE_ACTIVE=false
ARG EVENTNOTIFIERSERVICE_HOSTNAME=localhost
ARG EVENTNOTIFIERSERVICE_PORT=8006

ENV SPRING_PROFILES_ACTIVE ${SPRING_PROFILES_ACTIVE}
ENV SPRING_PORT ${SPRING_PORT}
ENV SPRING_PROPS ${SPRING_PROPS}
ENV JAVA_OPTS ${JAVA_OPTS}
ENV DATABASE_URL ${DATABASE_URL}
ENV DATABASE_USERNAME ${DATABASE_USERNAME}
ENV DATABASE_PASSWORD ${DATABASE_PASSWORD}
ENV FLYWAY_SCHEMA ${FLYWAY_SCHEMA}
ENV FLYWAY_SCRIPTS_DIR ${FLYWAY_SCRIPTS_DIR}
ENV H2_CONSOLE_ENABLED ${H2_CONSOLE_ENABLED}
ENV H2_CONSOLE_PATH ${H2_CONSOLE_PATH}
ENV METASERVICE_ACTIVE ${METASERVICE_ACTIVE}
ENV METASERVICE_HOSTNAME ${METASERVICE_HOSTNAME}
ENV METASERVICE_PORT ${METASERVICE_PORT}
ENV POLICYSERVICE_ACTIVE ${POLICYSERVICE_ACTIVE}
ENV POLICYSERVICE_HOSTNAME ${POLICYSERVICE_HOSTNAME}
ENV POLICYSERVICE_PORT ${POLICYSERVICE_PORT}
ENV EVENTNOTIFIERSERVICE_ACTIVE ${EVENTNOTIFIERSERVICE_ACTIVE}
ENV EVENTNOTIFIERSERVICE_HOSTNAME ${EVENTNOTIFIERSERVICE_HOSTNAME}
ENV EVENTNOTIFIERSERVICE_PORT ${EVENTNOTIFIERSERVICE_PORT}

EXPOSE $SPRING_PORT

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS  -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE $SPRING_PROPS -jar ./application.jar" ]