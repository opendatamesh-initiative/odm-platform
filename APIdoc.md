# API Static Documentation
In this section we document how we decided to recreate static documentation for our APIs.
The components that we use are Swagger, OpenAPI and Redoc.
## Runtime documentation with Swagger
First of all, each project exposes a Swagger endpoint at `<baseurl>/swagger-ui/index.html`.
This is done by adding the following dependency to its pom:
```xml
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-ui</artifactId>
            <version>1.6.11</version>
        </dependency> 
```

## OpenAPI 3.0 descriptor
Swagger exposes also a JSON definition of the APIs at `<baseurl>/v3/api-docs`.
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
					<apiDocsUrl>http://localhost:8001/api/v1/pp/v3/api-docs</apiDocsUrl> 
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
	redoc-cli build ./target/openapi.json  --options.hideHostname
	```
 2.  Moves the generated page into the target folder:
	```shell
	mv ./redoc-static.html ./target
	```

!!!tip

	In order to generate the HTML page redoc-cli command should be available on the machine performing the build. It can be installed through npm with the following command:
```shell
sudo npm i -g redoc-cli
```

!!!tip

	To install NPM, if missing, use the following command:
```shell
sudo apt install npm
```
