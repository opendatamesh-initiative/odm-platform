# Open Data Mesh Platform: CLI

CLI of the Open Data Mesh Platform.

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
### Compile parent project
Compile the project:

```bash
mvn clean install -DskipTests
```

### Compile the CLI
Compile the module:

```bash
cd odm-cli
mvn clean package spring-boot:repackage -DskipTests
```

### Run the CLI
For Linux systems, define an alias before executing the CLI commands.
```bash
alias odm-cli='java -jar target/odm-cli-1.0.0.jar'
```
*_version could be greater than 1.0.0, check on POM_

*_For Windows system, just use `java -jar target/odm-cli-1.0.0.jar` instead of `odm-cli` in the next steps_

Execute CLI commands:

```bash
odm-cli <command> [-<options>]
```

For commands documentation, use the _helper_ of the CLI:
```bash
odm-cli -h
```
or
```bash
odm-cli --help
```


*_Each command have it's helper, for example:_
```bash
odm-cli validate-dpv --help
```