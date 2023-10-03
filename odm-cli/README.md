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
mvn clean package spring-boot:repackage
```

### Run the CLI
Make sure to be in `odm-platform/odm-cli` directory.

#### Unix systems
```bash
./odm-cli <command> [-<options>]
```
*_if it won't run, make sure to execute `chmod +x odm-cli`_

For commands documentation, use the _helper_ of the CLI:
```bash
./odm-cli -h
```
or
```bash
./odm-cli --help
```
*_Each command have its helper, for example:_
```bash
./odm-cli validate-dpv --help
```

#### Windows Systems
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
*_Each command have its helper, for example:_
```bash
odm-cli validate-dpv --help
```

### Example
Validate the `dpd-example.json` in the `odm-cli/src/main/resources` directory.
Once in `odm-cli` directory and given the execution of the previous steps:

_Windows_:
```bash
odm-cli validate-dpv -f src/main/resources/dpds/dpd-example.json
```
_Unix_:
```bash
./odm-cli validate-dpv -f src/main/resources/dpds/dpd-example.json
```
or

_Windows_:
```bash
odm-cli validate-dpv --file src/main/resources/dpds/dpd-example.json
```
_Unix_:
```bash
./odm-cli validate-dpv --file src/main/resources/dpds/dpd-example.json
```
