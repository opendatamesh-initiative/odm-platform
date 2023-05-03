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

## Run in docker
<<<<<<< HEAD
### Docker
The project could be built and executed through its Dockerfile. 
In this scenario, only the Spring project will be executed, a MySQL DB or a PostgreSQL DB must be reachable for the application to run correctly.

Clone the repository, move to the project root folder and build the image through the following commands:
```
docker build -t odm-dpexperience-service . --build-arg DATABASE_URL=<DATABASE_URL> --build-arg DATABASE_USERNAME=<DATABASE_USERNAME> --build-arg DATABASE_PASSWORD=<DATABASE_PASSWORD>
```
where:
* DATABASE_URL: the JDBC string connection for the desired DB (e.g. *jdbc:postgresql://localhost:5432/odmpdb*)
* DATABASE_USERNAME, DATABASE_PASSWORD: desired credentials

Then, execute it:
```
docker run --name odm-dpexperience-service -p 8585:8585 -d odm-dpexperience-service
```

If the DB is running on *localhost*, add *--network=host* as a parameter for the *build* command and *--net host* for the *exec* command.
```
docker build -t odm-dpexperience-service . --network=host --build-arg DATABASE_URL=<DATABASE_URL> --build-arg DATABASE_USERNAME=<DATABASE_USERNAME> --build-arg DATABASE_PASSWORD=<DATABASE_PASSWORD>
docker run --name odm-dpexperience-service --net host -p 8585:8585 -d odm-dpexperience-service
```

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
* FLYWAY_SCRIPTS_DIR (could be: *h2*, *postgres* or *mysql*)
* H2_CONSOLE_ENABLED (override it only if the desired DB is an embedded H2) 
* H2_CONSOLE_PATH (override it only if the desired DB is an embedded H2)

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
