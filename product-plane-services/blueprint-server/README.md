# Open Data Mesh Platform: Blueprint Server

Blueprint server of the Open Data Mesh Platform. 

It allows to initialize projects starting from a remote blueprint. Actually, it supports the following Git provider:
* GitHub
* Azure DevOps

# Configurations

## Git Providers Configurations

### Azure DevOps

#### Register Open Data Mesh as a Service Principal
Service principals are security objects within Azure AD defining what an application can do in a given Azure tenant.

1. Login into your Azure Portal, go under **Azure Active Directory** and then **App registrations**
2. Create a **New registration** with a name you desire (e.g. `odm-app`)
3. Enter your `odm-app` registration and go under **Certificates & secrets**
4. Create a new **Client secret** by choosing the name and the expiration period you want
5. Copy the client secret value in a secure place, such as a password manager (you will need it for the ODM configuration)
6. Go under **API permission**, add new permission by selecting _Azure DevOps_ from the menu, and grant `user_impersonation` permission

#### Add the Service Principal to the Azure DevOps organization
Once the service principal is configured in Azure AD, you need to do the same in Azure DevOps.

1. Login into your Azure DevOps organization (`https://dev.azure.com/<your_organization_name>`) and go under **Organization settings**
2. Go under **Users** and add a new user by searching for the name of the service principal you created before
3. Grant `Basic` access level to the user

The service principal can now act as a real user on Azure DevOps in a machine-to-machine interaction.

#### Configure SSH
In order to allow the application to *clone* and *push* on repositories on Azure DevOps an SSH key must be generated on the host machine and added to the Azure DevOps Repositories.


## Useful resources
[Azure DevOps Services | Authenticate with service principals or managed identities](https://learn.microsoft.com/en-us/azure/devops/integrate/get-started/authentication/service-principal-managed-identity?view=azure-devops)


### GitHub

#### Create a Personal Access Token (i.e., PAT)
Create a Personal Access Token and configure it:
1. Login to GitHub and go under *Settings > Developer Settings > Personal access token*
2. Create a PAT, set the expiration date and select the *scopes* of the token; the **repo** scope must be selected 
3. Copy the resulting token and store it

#### Configure SSH
In order to allow the application to *clone* and *push* on repositories on GitHub an SSH key must be generated on the host machine and added to the GitHub settings. 
It could be done for a user profile, an organization or for single and specific repositories. It's possible to add more than one SSH key.

## App configuration

To run the application and to set up the OAuth 2.0 mechanism, you need to configure the following environment variables.

### Azure DevOps
Application profile: profile *dev-azuredevops*

#### Client ID
Set an environment variable called `OAUTH_CLIENT_ID`. This is the Application (client) ID of the service principal.

1. Login into your Azure Portal, go under **Azure Active Directory** and then **App registrations**
2. Search for the `odm-app` app registration
3. Go to the **Overview** page and retrieve the **Application (client) ID**

#### Client Secret
Set an environment variable called `OAUTH_CLIENT_SECRET`. This is the value of the secret you created during the Service Principal registration.

#### Tenant ID
Set an environment variable called `AZURE_TENANT_ID`. This is the Tenant ID of your Azure organization.

1. Login into your Azure Portal and go under **Azure Active Directory**
2. Retrieve the **Tenant ID**

####

### GitHub
Application profile: profile *dev-github*

#### PAT
Set an environment variable called `PERSONAL_ACCESS_TOKEN` containing the PAT created previously.

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
java -jar product-plane-services/blueprint-server/target/odm-platform-pp-blueprint-server-1.0.0.jar
```
*_version could be greater than 1.0.0, check on parent POM_

### Stop application
To stop the application type CTRL+C or just close the shell. To start it again re-execute the following command:

```bash
java -jar product-plane-services/blueprint-server/target/odm-platform-pp-blueprint-server-1.0.0.jar
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
docker run --name odmp-blueprint-mysql-db -d -p 3306:3306  \
   -e MYSQL_DATABASE=ODMBLUEPRINT \
   -e MYSQL_ROOT_PASSWORD=root \
   mysql:8
```

**Postgres**
```bash
docker run --name odmp-blueprint-postgres-db -d -p 5432:5432  \
   -e POSTGRES_DB=odmpdb \
   -e POSTGRES_USER=postgres \
   -e POSTGRES_PASSWORD=postgres \
   postgres:11-alpine
```

Check that the database has started correctly:

**MySql**
```bash
docker logs odmp-blueprint-mysql-db
```

*Postgres*
```bash
docker logs odmp-blueprint-postgres-db
```

### Build image
Build the Docker image of the application and run it.

*Before executing the following commands change properly the value of arguments `DATABASE_USERNAME`, `DATABASE_PASSWORD` and `DATABASE_URL`. Reported commands already contains right argument values if you have created the database using the commands above.

**MySql**
```bash
docker build -t odmp-blueprint-mysql-app . -f ./product-plane-services/blueprint-server/Dockerfile \
   --build-arg DATABASE_URL=jdbc:mysql://localhost:3306/ODMBLUEPRINT \
   --build-arg DATABASE_USERNAME=root \
   --build-arg DATABASE_PASSWORD=root \
   --build-arg FLYWAY_SCRIPTS_DIR=mysql \
   --build-arg <git-args>
```

**Postgres**
```bash
docker build -t odmp-blueprint-postgres-app . -f ./product-plane-services/blueprint-server/Dockerfile \
   --build-arg DATABASE_URL=jdbc:postgresql://localhost:5432/odmpdb \
   --build-arg DATABASE_USERNAME=postgres \
   --build-arg DATABASE_PASSWORD=postgres \
   --build-arg FLYWAY_SCRIPTS_DIR=postgresql \
   --build-arg <git-args>
```

*_`--build-arg <git-args>` changes depending on the Git provider:_
```bash
[Azure DevOps]
  --build-arg GIT_PROVIDER=AZURE_DEVOPS \
  --build-arg OAUTH_CLIENT_ID=<personal-access-token> \
  --build-arg OAUTH_CLIENT_SECRET=<personal-access-token> \
  --build-arg OAUTH_TOKEN_URI=<oauth-token-uri> \
  --build-arg OAUTH_SCOPE=<personal-access-token>
```
```bash
[GitHub]
  --build-arg GIT_PROVIDER=GITHUB \
  --build-arg PERSONAL_ACCESS_TOKEN=<personal-access-token>
```
*_this is different from the local app configuration, OAuth2 parameter `scope` must include repository privileges and OAuth2  parameter `token-uri` must be explciited as full URI_

### Run application
Run the Docker image.

*Note: Before executing the following commands remove the argument `--net host` if the database is not running on `localhost`*

**MySql**
```bash
docker run \
  --name odmp-blueprint-mysql-app \
  -p 8003:8003 \
  --net host \
  -v $HOME/.ssh:/root/.ssh \
  -v $SSH_AUTH_SOCK:/ssh-agent \
  -e SSH_AUTH_SOCK=/ssh-agent \
  odmp-blueprint-mysql-app
```

**Postgres**
```bash
docker run --name odmp-blueprint-postgres-app \
  -p 8003:8003 \
  --net host \
  -v $HOME/.ssh:/root/.ssh \
  -v $SSH_AUTH_SOCK:/ssh-agent \
  -e SSH_AUTH_SOCK=/ssh-agent \
  odmp-blueprint-postgres-app
```

*_SSH volume and agents must be added to the Docker execution in order to use them; It's also possible to do it in different ways, but it must be done to correctly execute the process._
### Stop application

*Before executing the following commands:
* change the DB name to `odmp-blueprint-postgres-db` if you are using postgres and not mysql
* change the instance name to `odmp-blueprint-postgres-app` if you are using postgres and not mysql

```bash
docker stop odmp-blueprint-mysql-app
docker stop odmp-blueprint-mysql-db
```
To restart a stopped application execute the following commands:

```bash
docker start odmp-blueprint-mysql-db
docker start odmp-blueprint-mysql-app
```

To remove a stopped application to rebuild it from scratch execute the following commands :

```bash
docker rm odmp-blueprint-mysql-app
docker rm odmp-blueprint-mysql-db
```

## Run with Docker Compose

### Clone repository
Clone the repository and move to the project root folder

```bash
git clone git@github.com:opendatamesh-initiative/odm-platform.git
cd odm-platform/product-plane-services/blueprint-server
```

### Build image
Build the docker-compose images of the application and a default PostgreSQL DB (v11.0).

Before building it, create a `.env` file in the blueprint-server directory of the project similar to the following one:
```.dotenv
DATABASE_NAME=odmpdb
DATABASE_PASSWORD=root
DATABASE_USERNAME=root
DATABASE_PORT=5432
SPRING_PORT=8003
GIT_PROVIDER=<git-provider>
OAUTH_TOKEN_URI=<oauth2-token-uri>
OUATH_CLIENT_ID=<oauth2-client-id>
OAUTH_CLIENT_SECRET=<oauth2-client-secret>
OAUTH_SCOPE=<oauth2-scope>
PERSONAL_ACCESS_TOKEN=<personal-access-token>
```
*_Remember that `GIT_PROVIDER` should be one of `[AZURE_DEVOPS, GITHUB]`; For the former set the `PERSONAL_ACCESS_TOKEN` as `null` and populate the `OAUTH` fields; for the latter the opposite_ 

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

* [http://localhost:8003/api/v1/pp/blueprint/swagger-ui/index.html](http://localhost:8003/api/v1/pp/blueprint/swagger-ui/index.html)

## Database

If the application is running using an in memory instance of H2 database you can check the database content through H2 Web Console available at the following url:

* [http://localhost:8003/api/v1/pp/blueprint/h2-console](http://localhost:8003/api/v1/pp/blueprint/h2-console)

In all cases you can also use your favourite sql client providing the proper connection parameters
