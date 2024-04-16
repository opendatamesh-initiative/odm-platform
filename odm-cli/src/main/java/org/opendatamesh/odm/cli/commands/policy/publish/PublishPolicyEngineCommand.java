package org.opendatamesh.odm.cli.commands.policy.publish;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.odm.cli.utils.FileReaderUtils;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEngineResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

import java.io.IOException;

@Command(
        name = "engine",
        description = "Publish a Policy Engine",
        version = "odm-cli policy publish engine 1.0.0",
        mixinStandardHelpOptions = true
)
public class PublishPolicyEngineCommand implements Runnable {

    @ParentCommand
    private PolicyPublishCommand policyPublishCommand;

    private static final ObjectMapper objectMapper = ObjectMapperFactory.JSON_MAPPER;

    @Option(
            names = "--engine-file",
            description = "Path of the JSON descriptor of the Policy Engine object",
            required = true
    )
    String policyEngineDescriptorPath;

    @Override
    public void run() {
        PolicyEngineResource policyEngine;
        try {
            policyEngine = objectMapper.convertValue(
                    FileReaderUtils.readFileFromPath(policyEngineDescriptorPath),
                    PolicyEngineResource.class
            );
        } catch (IOException e) {
            System.out.println(
                    "Impossible to read file [" + policyEngineDescriptorPath + "]. Check if the file exists and retry"
            );
            return;
        }
        try {
            ResponseEntity<PolicyEngineResource> engineResponseEntity =
                    policyPublishCommand.policyCommands.getPolicyClient().createPolicyEngineResponseEntity(policyEngine);
            if(engineResponseEntity.getStatusCode().equals(HttpStatus.CREATED)) {
                policyEngine = engineResponseEntity.getBody();
                System.out.println("Policy Engine CREATED:\n" + objectMapper.writeValueAsString(policyEngine));
            }
            else
                System.out.println("Got an unexpected response. Error code: " + engineResponseEntity.getStatusCode());
        } catch (ResourceAccessException e) {
            System.out.println("Impossible to connect with Policy server. Verify the URL and retry");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
