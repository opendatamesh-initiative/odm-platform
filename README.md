# Open Data Mesh Platform

[![Build](https://github.com/opendatamesh-initiative/odm-platform/workflows/odm-platform%20CI/badge.svg)](https://github.com/opendatamesh-initiative/odm-platform/actions) [![Release](https://github.com/opendatamesh-initiative/odm-platform/workflows/odm-platform%20CI%2FCD/badge.svg)](https://github.com/opendatamesh-initiative/odm-platform/actions)

Open Data Mesh Platform is a platform that manages the full lifecycle of a data product from deployment to retirement. It use the [Data Product Descriptor Specification](https://dpds.opendatamesh.org/) to to create, deploy and operate data product containers in a mesh architecture. 

# Run it

## Prerequisites
The project requires the following dependencies:
* Java 11
* Maven 3.8.6

## Run locally

### Clone repository
Clone the repository and move to the project root folder

```bash
git clone git@github.com:opendatamesh-initiative/odm-platform-pp-services.git
cd odm-platform-pp-services
```
### Compile project
Compile the project:

```bash
mvn clean install -DskipTests
```

### Run application
Run the application:

```bash
java -jar registry-server/target/odm-platform-pp-registry-server-1.0.0.jar
```

*_version could be greater than 1.0.0, check on parent POM_

### Stop application
To stop the application type CTRL+C or just close the shell. To start it again re-execute the following command:

```bash
java -jar registry-server/target/odm-platform-pp-registry-server-1.0.0.jar
```
*Note: The application run in this way uses an in-memory instance of the H2 database. For this reason, the data is lost every time the application is terminated. On the next restart, the database is recreated from scratch.*

## Run with Docker

### Clone repository
Clone the repository and move it to the project root folder

```bash
git clone git@github.com:opendatamesh-initiative/odm-platform-pp-services.git
cd odm-platform-pp-services
```

Here you can find the Dockerfile which creates an image containing the application by directly copying it from the build executed locally (i.e. from `target` folder).

### Compile project
You need to first execute the build locally by running the following command: 

```bash
mvn clean install -DskipTests
```

### Run database
The image generated from Dockerfile contains only the application. It requires a database to run properly. The supported databases are MySql and Postgres. If you do not already have a database available, you can create one by running the following commands:

**MySql**
```bash
docker run --name odmp-mysql-db -d -p 3306:3306  \
   -e MYSQL_DATABASE=ODMREGISTRY \
   -e MYSQL_ROOT_PASSWORD=root \
   mysql:8
```

**Postgres**
```bash
docker run --name odmp-postgres-db -d -p 5432:5432  \
   -e POSTGRES_DB=odmpdb \
   -e POSTGRES_USER=postgres \
   -e POSTGRES_PASSWORD=postgres \
   postgres:11-alpine
```

Check that the database has started correctly:

**MySql**
```bash
docker logs odmp-mysql-db
```

*Postgres*
```bash
docker logs odmp-postgres-db
```

### Build image
Build the Docker image of the application and run it. 

*Before executing the following commands change properly the value of arguments `DATABASE_USERNAME`, `DATABASE_PASSWORD` and `DATABASE_URL`. Reported commands already contains right argument values if you have created the database using the commands above.

**MySql**
```bash
docker build -t odmp-mysql-app . -f Dockerfile \
   --build-arg DATABASE_URL=jdbc:mysql://localhost:3306/ODMREGISTRY \
   --build-arg DATABASE_USERNAME=root \
   --build-arg DATABASE_PASSWORD=root \
   --build-arg FLYWAY_SCRIPTS_DIR=mysql
```

**Postgres**
```bash
docker build -t odmp-postgres-app . -f Dockerfile \
   --build-arg DATABASE_URL=jdbc:postgresql://localhost:5432/odmpdb \
   --build-arg DATABASE_USERNAME=postgres \
   --build-arg DATABASE_PASSWORD=postgres \
   --build-arg FLYWAY_SCRIPTS_DIR=postgresql
```

### Run application
Run the Docker image. 

*Note: Before executing the following commands remove the argument `--net host` if the database is not running on `localhost`*

**MySql**
```bash
docker run --name odmp-mysql-app -p 8585:8585 --net host odmp-mysql-app
```

**Postgres**
```bash
docker run --name odmp-postgres-app -p 8585:8585 --net host odmp-postgres-app
```

### Stop application

*Before executing the following commands: 
* change the DB name to `odmp-postgres-db` if you are using postgres and not mysql
* change the instance name to `odmp-postgres-app` if you are using postgres and not mysql

```bash
docker stop odmp-mysql-app
docker stop odmp-mysql-db
```
To restart a stopped application execute the following commands:

```bash
docker start odmp-mysql-db
docker start odmp-mysql-app
```

To remove a stopped application to rebuild it from scratch execute the following commands :

```bash
docker rm odmp-mysql-app
docker rm odmp-mysql-db
```

## Run with Docker Compose

### Clone repository
Clone the repository and move it to the project root folder

```bash
git clone git@github.com:opendatamesh-initiative/odm-platform-pp-services.git
cd odm-platform-pp-services
```

### Build image
Build the docker-compose images of the application and a default PostgreSQL DB (v11.0).

Before building it, create a `.env` file in the root directory of the project similar to the following one:
```.dotenv
DATABASE_NAME=odmpdb
DATABASE_PASSWORD=pwd
DATABASE_USERNAME=usr
DATABASE_PORT=5432
SPRING_PORT=8585
```
*_if the DB will be MySQL use the Schema name (i.e., ODMREGISTRY) as DATABASE_NAME_

Then, build the docker-compose file:
```bash
docker-compose build
```

### Run application
Run the docker-compose images.
```bash
docker-compose up
```

### Stop application
Stop the docker-compose images
```bash
docker-compose down
```
To restart a stopped application execute the following commands:

```bash
docker-compose up
```

To rebuild it from scratch execute the following commands :
```bash
docker-compose build --no-cache
```

# Test it

## REST services

You can invoke REST endpoints through *OpenAPI UI* available at the following url:

* [http://localhost:8585/api/v1/pp/swagger-ui/index.html](http://localhost:8585/api/v1/pp/swagger-ui/index.html)

## Database 

If the application is running using an in memory instance of H2 database you can check the database content through H2 Web Console available at the following url:

* [http://localhost:8585/api/v1/pp/h2-console](http://localhost:8585/api/v1/pp/h2-console)

In all cases you can also use your favourite sql client providing the proper connection parameters
