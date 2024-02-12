# API Static Documentation
In this section we document how we decided to recreate static documentation for our APIs.
The components that we use are Swagger, OpenAPI and Redoc.

## Runtime documentation with Swagger
First of all, each project exposes a Swagger endpoint at `<baseUrl>/swagger-ui/index.html`.
This is done by adding the following dependency to its pom:
```xml
<dependency>
	<groupId>org.springdoc</groupId>
	<artifactId>springdoc-openapi-ui</artifactId>
	<version>1.6.11</version>
</dependency> 
```

## OpenAPI 3.0 descriptor
Swagger exposes also a JSON definition of the APIs at `<baseUrl>/v3/api-docs`.
Since we need it in order to generate Redoc static HTML page, we added an execution in the pom in order to generate a json file in the target folder when code is builded.
This is done modifying spring-boot-maven-plugin and adding springdoc-openapi-maven-plugin as follows:
```xml
<plugin>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-maven-plugin</artifactId>
	<version>2.3.3.RELEASE</version>
	<configuration>
		<wait>1000</wait>
		<maxAttempts>180</maxAttempts>
	</configuration>
	<executions>
		<execution>
			<id>pre-integration-test</id>
			<goals>
				<goal>start</goal>
			</goals>
		</execution>
		<execution>
			<id>post-integration-test</id>
			<goals>
				<goal>stop</goal>
			</goals>
		</execution>
	</executions>
</plugin>
<plugin>
	<groupId>org.springdoc</groupId>
	<artifactId>springdoc-openapi-maven-plugin</artifactId>
	<version>0.2</version>
	<executions>
		<execution>
			<phase>integration-test</phase>
			<goals>
				<goal>generate</goal>
			</goals>
		</execution>
	</executions>
	 <configuration> 
		<apiDocsUrl>{baseUrl}/v3/api-docs</apiDocsUrl> <!-- baseUrl example: http://localhost:8001/api/v1/pp/registry -->
		<outputFileName>openapi.json</outputFileName> 
		<outputDir>${project.build.directory}</outputDir> 
	</configuration>
</plugin>
```

## Redoc static HTML page
The final step is to generate the static HTML page with Redoc.
We do it with a script, located in the scripts folder, called at build time with the following plugin added to the pom:
```xml
<plugin>
	<artifactId>exec-maven-plugin</artifactId>
	<groupId>org.codehaus.mojo</groupId>
	<version>3.1.0</version>
	<executions>
		<execution>
			<id>redoc-static-html-gen</id>
			<phase>post-integration-test</phase>
			<goals>
				<goal>exec</goal>
			</goals>
			<configuration>
				<executable>${basedir}/scripts/redoc-static-html-gen.sh</executable>
			</configuration>
		</execution>
	</executions>
</plugin>
```

The script does two simple things:
1. Generates the static HTML page with the following command:
```shell
redocly build-docs ./target/openapi.json --output redoc-static.html --theme.openapi.hideHostname
```
2.  Moves the generated page into the target folder:
```shell
mv ./redoc-static.html ./target
```

!!!tip

In order to generate the HTML page `redocly/cli` command should be available on the machine performing the build. It can be installed through npm with the following command:
```shell
sudo npm i -g @redocly/cli@latest
```

!!!tip

To install NPM, if missing, use the following command:
```shell
sudo apt install npm
```

## Profiles and Doc generation
In order to avoid to create the documentation at each maven command, the `<build>` attribute parts relative to that task is moved in a `<profile>` attribute of the `pom`.
This lead to the possibility of generating the documentation only when needed, specifing the profile in the desired maven command.
```xml
<project>
	
	<!-- rest of the pom -->
	
	<build>
		<!-- ... --->
	</build>
	
	<profiles>
		<profile>
			<id>generate-doc</id>
			<build>
				<!-- Plugins for doc generation -->
			</build>
		</profile>
	</profiles>
	
</project>
```
Now, to generate the doc for the desired module, the command will be:
```bash
mvn clean verify -DskipTests -Pgenerate-doc
```

After the execution, in the `target` directory of the module will reside a `redoc-static.html` file containing the API doc.

## Single projects and full projects documentation
The behaviour explained above works for every single module, so, considering `odm-platform` as the current working directory,
every module exposing API allow to generate the documentation as follows:
```bash
cd <module-path>
mvn clean verify -DskipTests -Pgenerate-doc
```

In addition to this, a script in the root directory allow to generate the documentation for each module
and aggregate them in a new `redocly-docs` directory on the `root` level (i.e., that is `odm-platform`).

```bash
sh generate-full-redoc-static-html.sh
```

After the generation, it's also possible to upload the static files to an AWS S3 Bucket with the sript `upload-redoc-static-html-to-s3` on the `root` level.
This script require:
* AWS CLI installed
* 2 parameters:
	* first one, the version of the doc to upload (e.g., _v1.0.0_)
	* second one, the name of the AWS S3 Bucket (default value for the official ODM S3 Bucket: _odmdocbucket_)
* 3 environment variables:
	* AWS_ACCESS_KEY_ID
	* AWS_SECRET_ACCESS_KEY
	* AWS_DEFAULT_REGION (default value for the official ODM S3 Bucket: _eu-central-1_)

An example of execution could be:
```bash
sh upload-redoc-static-html-to-s3 v1.0.0 odmdocbucket
```