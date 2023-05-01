# README #
## Summary ##

* Content: Open Data Mesh Data Product Experience Plane
* Version: 0.1

## Description
This repository contains:

* a springboot project for the backend of **Open Data Mesh Data Product Experience Plane**
* a Docker project for its execution


## Requirements ##
* Java 11
* Maven
* IDE (raccomanded IntelliJ)
* Git
* Docker & docker-compose

## Development ##
Clone the repo, create a new development branch and then open the project with the selected IDE.

## Execution ##
Before running the application, set the environment variable SPRING_PROFILES_ACTIVE to the desired Spring profile.
In Intellij it could be done through *Run > Edit Configurations > Environment Variables*

App could also be executed through *docker-compose* after the creation of a `.env` file. An example of a valid `.env` file could be:
```
APP_NAME=odm-dpexperience-service
JAVA_OPTS=
SPRING_PROFILES_ACTIVE=docker
SPRING_LOCAL_PORT=8585
SPRING_DOCKER_PORT=8585
DATABASE_URL=jdbc:h2:mem:testdb
DATABASE_USERNAME=sa
DATABASE_PASSWORD=
SPRING_DATABASE_DIALECT=org.hibernate.dialect.H2Dialect
SPRING_DATABASE_DRIVER=org.h2.Driver
FLYWAY_SCHEMA=flyway
FLYWAY_SCRIPTS_DIR=h2
```

## Deploy ##
**Requirement: Docker**

It is possible to deploy the application by building the docker image with the following commands:

* Change the current directory in your terminal to the main directory of the repository (where the **Dockerfile** file is placed)
* `docker build -t odm-dpexperience-service . --build-arg SPRING_PROFILES_ACTIVE=<SPRING_PROFILES_ACTIVE> --build-arg APP_NAME=odm-dpexperience-service --build-arg JAVA_OPTS=<JAVA_OPTS> --build-arg ...` 
  where an example for all the args could be:
    * `<SPRING_PROFILES_ACTIVE>` = docker
    * `<APP_NAME>` = odm-dpexperience-service
    * `<JAVA_OPTS>` = ""
    * `<DATABASE_URL>` = jdbc:h2:mem:testdb
    * `<DATABASE_USERNAME>` = sa
    * `<DATABASE_PASSWORD>` = ""
    * `<SPRING_DATABASE_DIALECT>` = org.hibernate.dialect.H2Dialect
    * `<SPRING_DATABASE_DRIVER>` = org.h2.Driver
    * `<FLYWAY_SCHEMA>` = flyway
    * `<FLYWAY_SCRIPTS_DIR>` = h2
* `docker run --name odm-dpexperience-service -p 8585:8585 -d odm-dpexperience-service`

## Usage ##
Once the application is started, you may find the API documentation and a Swagger at this [URL](http://localhost:8585/swagger-ui/index.html).

## Contributors ##

* Pietro La Torre
* Andrea Gioia
* Mattia Pennati

# Notes #

## Currently implemented APIs

### CREATE

1. **POST** to create a dataproduct
2. **GET** to get a list of dataproducts (that may be filtered by _status, domain, ownerId_)
3. **GET** to get one dataproduct given its id (that may be filtered by component type with the _projection_ parameter)
4. **PATCH** to add or modify some dataproduct's dataProductInfoRes or one dataproduct's component (inputport, outputport, controlport, discoveryport, applicationComponent, infrastructuralComponent)
6. **DELETE** to delete a dataproduct

### BUILD

1. **POST** to create a build
2. **GET** to get a build given its id
3. **GET** to get the application build list referred to a build, given its id
4. **GET** to get the list of builds (that may be filtered by _dataproduct ID, status or environment_)

### DEPLOY

1. **POST** to create a new deployment - it will change the _previousActiveDeployment_ and _endDate_ attributes of the deployment of a previous version of the data product, if exists
2. **GET** to get the list of all deployments  (that may be filtered by _env, status, active, dataproductId_)
3. **GET** to get a deployment given its id
4. **GET** to get the deployments' details given its id

Moreover, every 10 seconds the DEPLOYMENTs with status other than DEPLOYED, UNDEPLOYED or FAILED will call the provisioner (infrastructure, build or application integration depending on their status) to update their status and informations.

## Usefull Links
1. [Swagger UI](http://localhost:8585/api/v1/pp/swagger-ui/index.html)
2. [h2 console](http://localhost:8585/api/v1/pp/h2-console/)

## Utilities
In the *utils* directory there are:

* an example of a data product descriptor (useful to call the CREATE of a data product)
* an example of a deployment descriptor (useful to request the deploy of the previous created dataproduct)
* an example of a build descriptor (useful to request the build of the previous created dataproduct)