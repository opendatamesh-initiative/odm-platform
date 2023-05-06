# Open Data Mesh Platform Product Plane

Open Data Mesh Platform is a platform that manages the full lifecycle of a data product from deployment to retirement. It use the [Data Product Descriptor Specification](https://dpds.opendatamesh.org/) to to create, deploy and operate data product containers in a mesh architecture. This repository contains the services exposed by the platform product plane.

# Run it

## Prerequisites
The project requires the following dependencies:

* Java 11
* Maven 3.8.6

## Run locally
Clone the repository and move to the project root folder

```
git clone git@github.com:opendatamesh-initiative/odm-platform-pp-services.git
cd odm-platform-pp-services
```

Compile the project:
```
mvn clean install
```

Run the application:
```
java -jar target/odm-platform-pp-1.0.0.jar 
```

Alternatively, it's possible to use a IDE like IntelliJ to import and run the application.

## Run with Docker

Clone the repository and move to the project root folder

```
git clone git@github.com:opendatamesh-initiative/odm-platform-pp-services.git
cd odm-platform-pp-services
```

Here you can find the following two Dockerfiles:
* **Dockerfile:** This file creates a docker image containing the application built from the code present on the Git repository;
* **Dokerfile.local:** This file creates an image containing the application by directly copying it from the build executed locally (i.e. from `target` folder).

If you decide to create the Docker image using the second Dockerfile (i.e. `Dokerfile.local`), you need to first execute the build locally by running the following command: 

```bash
mvn clean install
```

The image generated from both Dockerfiles contains only the application. It requires a database to run properly. The supported databases are MySql and Postgres. If you do not already have a database available, you can create one by running the following commands:

*MySql*
```
docker run --name odmp-mysql-db -d -p 3306:3306  -e MYSQL_DATABASE=odmpdb -e MYSQL_ROOT_PASSWORD=root mysql:8
```

*Postgres*
```
docker run --name odmp-postgres-db -d -p 5432:5432  -e POSTGRES_DB=odmpdb -e POSTGRES_PASSWORD=postgres postgres:11-alpine
```

Check that the database has started correctly:

*MySql*
```
docker logs odmp-mysql-db
```

*Postgres*
```
docker logs odmp-mysql-db
```

Build the Docker image of the application and run it. 

*Note: Before executing the following commands change properly the value of arguments `DATABASE_USERNAME`, `DATABASE_PASSWORD` and `DATABASE_URL`. Reported commands already contains right argument values if you have created the database using the commands above.*

**MySql**
```
docker build -t odmp-mysql-app . -f Dockerfile.local --build-arg DATABASE_URL=jdbc:mysql://localhost:3306/odmpdb --build-arg DATABASE_USERNAME=root --build-arg DATABASE_PASSWORD=root --build-arg FLYWAY_SCRIPTS_DIR=mysql
```

**Postgres**
```
docker build -t odmp-postgres-app . -f Dockerfile.local --build-arg DATABASE_URL=jdbc:postgresql://localhost:5432/odmpdb --build-arg DATABASE_USERNAME=postgres --build-arg DATABASE_PASSWORD=postgres --build-arg FLYWAY_SCRIPTS_DIR=postgres
```
Run the Docker image. 

*Note: Before executing the following commands remove the argument `--net host` if the database is not running on `localhost`*

**MySql**
```
docker run --name odmp-mysql-app -p 8585:8585 --net host odmp-mysql-app
```

**Postgres**
```
docker run --name ododmp-postgres-appmp -p 8585:8585 --net host odmp-postgres-app
```



## Run with Docker Compose


It's possible to find tested docker-compose version of MySQL and PostgreSQL DBs under:
* *src/main/resources/mysql*
* *src/main/resources/postgres*

It's possible to override several arguments to control, adding them to the previous commands, in order to obtain more control of the project.
The full list of *build_arg* possible is:
* SPRING_PROFILES_ACTIVE
* SPRING_LOCAL_PORT
* JAVA_OPTS
* DATABASE_URL
* DATABASE_USERNAME
* DATABASE_PASSWORD
* FLYWAY_SCHEMA
* FLYWAY_SCRIPTS_DIR
    * could only be *h2*, *postgres* or *mysql*
    * specify which SQL files use to start the DB
* H2_CONSOLE_ENABLED (override it only if the desired DB is an embedded H2)
    * could be *true*, *false* (default *false*)
    * override it only if the desired DB is embedded H2
* H2_CONSOLE_PATH
    * default *h2-console*
    * specify it only if H2_CONSOLE_ENABLED=true
    * override it only if the desired DB is an embedded H2

### docker-compose
The project and a default embedded PostgreSQL DB could be executed through docker-compose.

Clone the repository, move to the project root folder and create a *.env* file similar to this:
```
DATABASE_PORT=5432
SPRING_LOCAL_PORT=8585
SPRING_DOCKER_PORT=8585
DATABASE_NAME=odmpdb
DATABASE_USERNAME=usr
DATABASE_PASSWORD=pwd
```

Then, execute the following command:
```
docker-compose up
```
