# Open Data Mesh Platform: Params Server

Params server of the Open Data Mesh Platform. 

# Run it

## Prerequisites
The project requires the following dependencies:
* Java 11
* Maven 3.8.6

## Run locally

### Clone repository
Clone the repository and move to the project root folder

```bash
git clone git@github.com:opendatamesh-initiative/odm-platform.git
cd odm-platform
```
### Compile project
Compile the project:

```bash
mvn clean install -DskipTests
```

### Run application
Run the application:

```bash
java -jar product-plane-services/params-server/target/odm-platform-pp-params-server-1.0.0.jar
```
*_version could be greater than 1.0.0, check on parent POM_

### Stop application
To stop the application type CTRL+C or just close the shell. To start it again re-execute the following command:

```bash
java -jar product-plane-services/params-server/target/odm-platform-pp-params-server-1.0.0.jar
```
*Note: The application run in this way uses an in-memory instance of the H2 database. For this reason, the data is lost every time the application is terminated. On the next restart, the database is recreated from scratch.*

## Run with Docker

### Clone repository
Clone the repository and move it to the project root folder

```bash
git clone git@github.com:opendatamesh-initiative/odm-platform.git
cd odm-platform
```

### Compile project
Execute the build locally by running the following command:

```bash
mvn clean install -DskipTests
```

### Run database
The image generated from Dockerfile contains only the application. It requires a database to run properly. The supported databases are MySql and Postgres. If you do not already have a database available, you can create one by running the following commands:

**MySql**
```bash
docker run --name odmp-params-mysql-db -d -p 3306:3306  \
   -e MYSQL_DATABASE=ODMBLUEPRINT \
   -e MYSQL_ROOT_PASSWORD=root \
   mysql:8
```

**Postgres**
```bash
docker run --name odmp-params-postgres-db -d -p 5432:5432  \
   -e POSTGRES_DB=odmpdb \
   -e POSTGRES_USER=postgres \
   -e POSTGRES_PASSWORD=postgres \
   postgres:11-alpine
```

Check that the database has started correctly:

**MySql**
```bash
docker logs odmp-params-mysql-db
```

*Postgres*
```bash
docker logs odmp-params-postgres-db
```

### Build image
Build the Docker image of the application and run it.

*Before executing the following commands change properly the value of arguments `DATABASE_USERNAME`, `DATABASE_PASSWORD` and `DATABASE_URL`. Reported commands already contains right argument values if you have created the database using the commands above.

**MySql**
```bash
docker build -t odmp-params-mysql-app . -f ./product-plane-services/params-server/Dockerfile \
   --build-arg DATABASE_URL=jdbc:mysql://localhost:3306/ODMBLUEPRINT \
   --build-arg DATABASE_USERNAME=root \
   --build-arg DATABASE_PASSWORD=root \
   --build-arg FLYWAY_SCRIPTS_DIR=mysql \
   --build-arg <git-args>
```

**Postgres**
```bash
docker build -t odmp-params-postgres-app . -f ./product-plane-services/params-server/Dockerfile \
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
docker run \
  --name odmp-params-mysql-app \
  -p 8003:8003 \
  --net host \
  -v $HOME/.ssh:/root/.ssh \
  -v $SSH_AUTH_SOCK:/ssh-agent \
  -e SSH_AUTH_SOCK=/ssh-agent \
  odmp-params-mysql-app
```

**Postgres**
```bash
docker run --name odmp-params-postgres-app \
  -p 8003:8003 \
  --net host \
  -v $HOME/.ssh:/root/.ssh \
  -v $SSH_AUTH_SOCK:/ssh-agent \
  -e SSH_AUTH_SOCK=/ssh-agent \
  odmp-params-postgres-app
```

*_SSH volume and agents must be added to the Docker execution in order to use them; It's also possible to do it in different ways, but it must be done to correctly execute the process._
### Stop application

*Before executing the following commands:
* change the DB name to `odmp-params-postgres-db` if you are using postgres and not mysql
* change the instance name to `odmp-params-postgres-app` if you are using postgres and not mysql

```bash
docker stop odmp-params-mysql-app
docker stop odmp-params-mysql-db
```
To restart a stopped application execute the following commands:

```bash
docker start odmp-params-mysql-db
docker start odmp-params-mysql-app
```

To remove a stopped application to rebuild it from scratch execute the following commands :

```bash
docker rm odmp-params-mysql-app
docker rm odmp-params-mysql-db
```

## Run with Docker Compose

### Clone repository
Clone the repository and move to the project root folder

```bash
git clone git@github.com:opendatamesh-initiative/odm-platform.git
cd odm-platform/product-plane-services/params-server
```

### Build image
Build the docker-compose images of the application and a default PostgreSQL DB (v11.0).

Before building it, create a `.env` file in the params-server directory of the project similar to the following one:
```.dotenv
DATABASE_NAME=odmpdb
DATABASE_PASSWORD=root
DATABASE_USERNAME=root
DATABASE_PORT=5432
SPRING_PORT=8003
```

*_Database name, port, password and parameters, as well as spring port, could be changed_

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

* [http://localhost:8003/api/v1/pp/params/swagger-ui/index.html](http://localhost:8003/api/v1/pp/params/swagger-ui/index.html)

## Database

If the application is running using an in memory instance of H2 database you can check the database content through H2 Web Console available at the following url:

* [http://localhost:8003/api/v1/pp/params/h2-console](http://localhost:8003/api/v1/pp/params/h2-console)

In all cases you can also use your favourite sql client providing the proper connection parameters
