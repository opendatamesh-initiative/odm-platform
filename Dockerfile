# Stage 1
FROM maven:3-openjdk-11-slim as build

WORKDIR /workspace/app

RUN apt-get update
RUN apt-get -y install git
RUN git clone https://github.com/opendatamesh-initiative/odm-platform-pp-services.git

WORKDIR /workspace/app/odm-platform-pp-services

RUN mvn clean install -DskipTests

# Stage 2
FROM openjdk:11-jre-slim

RUN apt-get update && apt-get install -y wget gpg lsb-release zip curl npm
RUN npm i -g redoc-cli

ARG SPRING_PROFILES_ACTIVE=docker
ARG SPRING_PORT=8585
ARG JAVA_OPTS
ARG DATABASE_URL
ARG DATABASE_USERNAME
ARG DATABASE_PASSWORD
ARG FLYWAY_SCHEMA=flyway
ARG FLYWAY_SCRIPTS_DIR=mysql
ARG H2_CONSOLE_ENABLED=false
ARG H2_CONSOLE_PATH=h2-console
ARG SKIP_METASERVICE=true
ARG METASERVICE_HOSTNAME=localhost
ARG METASERVICE_PORT=8595
ARG SKIP_POLICYSERVICE=true
ARG POLICYSERVICE_HOSTNAME=localhost
ARG POLICYSERVICE_PORT=4242
ENV SPRING_PROFILES_ACTIVE ${SPRING_PROFILES_ACTIVE}
ENV SPRING_PORT ${SPRING_PORT}
ENV JAVA_OPTS ${JAVA_OPTS}
ENV DATABASE_URL ${DATABASE_URL}
ENV DATABASE_USERNAME ${DATABASE_USERNAME}
ENV DATABASE_PASSWORD ${DATABASE_PASSWORD}
ENV FLYWAY_SCHEMA ${FLYWAY_SCHEMA}
ENV FLYWAY_SCRIPTS_DIR ${FLYWAY_SCRIPTS_DIR}
ENV H2_CONSOLE_ENABLED ${H2_CONSOLE_ENABLED}
ENV H2_CONSOLE_PATH ${H2_CONSOLE_PATH}
ENV SKIP_METASERVICE ${SKIP_METASERVICE}
ENV METASERVICE_HOSTNAME ${METASERVICE_HOSTNAME}
ENV METASERVICE_PORT ${METASERVICE_PORT}
ENV SKIP_POLICYSERVICE ${SKIP_POLICYSERVICE}
ENV POLICYSERVICE_HOSTNAME ${POLICYSERVICE_HOSTNAME}
ENV POLICYSERVICE_PORT ${POLICYSERVICE_PORT}

COPY --from=build  /workspace/app/odm-platform-pp-services/product-plane-services/registry-server/target/odm-platform-pp-*.jar /app/

RUN ln -s -f /usr/share/zoneinfo/Europe/Rome /etc/localtime

CMD java $JAVA_OPTS -jar /app/odm-platform-pp-registry-server*.jar --spring.profiles.active=$SPRING_PROFILES_ACTIVE

EXPOSE $SPRING_PORT