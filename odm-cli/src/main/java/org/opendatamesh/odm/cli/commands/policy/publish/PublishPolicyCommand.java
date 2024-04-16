package org.opendatamesh.odm.cli.commands.policy.publish;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.odm.cli.utils.FileReaderUtils;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;
import picocli.CommandLine.Option;

import java.io.IOException;

@Command(
        name = "policy",
        description = "Publish a Policy",
        version = "odm-cli policy publish policy 1.0.0",
        mixinStandardHelpOptions = true
)
public class PublishPolicyCommand implements Runnable {

    @ParentCommand
    private PolicyPublishCommand policyPublishCommand;

    private static final ObjectMapper objectMapper = ObjectMapperFactory.JSON_MAPPER;

    @Option(
            names = "--policy-file",
            description = "Path of the JSON descriptor of the Policy object",
            required = true
    )
    String policyDescriptorPath;

    @Override
    public void run() {
        PolicyResource policy;
        try {
            policy = objectMapper.convertValue(
                    FileReaderUtils.readFileFromPath(policyDescriptorPath),
                    PolicyResource.class
            );
        } catch (IOException e) {
            System.out.println(
                    "Impossible to read file [" + policyDescriptorPath + "]. Check if the file exists and retry"
            );
            return;
        }
        try {
            ResponseEntity<PolicyResource> policyResponseEntity =
                    policyPublishCommand.policyCommands.getPolicyClient().createPolicyResponseEntity(policy);
            if(policyResponseEntity.getStatusCode().equals(HttpStatus.CREATED)) {
                policy = policyResponseEntity.getBody();
                System.out.println("Policy CREATED:\n" + objectMapper.writeValueAsString(policy));
            }
            else
                System.out.println("Got an unexpected response. Error code: " + policyResponseEntity.getStatusCode());
        } catch (ResourceAccessException e) {
            System.out.println("Impossible to connect with Policy server. Verify the URL and retry");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}