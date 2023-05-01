# Open Data Mesh Platfor Product Plane

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

## Run in docker
TBD