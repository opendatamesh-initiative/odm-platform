package org.opendatamesh.odm.cli.commands.policy.update;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.odm.cli.utils.FileReaderUtils;
import org.opendatamesh.odm.cli.utils.ObjectMapperUtils;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

import java.io.IOException;

@Command(
        name = "policy",
        description = "Update a Policy",
        version = "odm-cli policy update policy 1.0.0",
        mixinStandardHelpOptions = true
)
public class UpdatePolicyCommand implements Runnable {

    @ParentCommand
    private PolicyUpdateCommand policyUpdateCommand;

    @Option(
            names = "--policy-file",
            description = "Path of the JSON descriptor of the Policy object",
            required = true
    )
    String policyDescriptorPath;

    @Option(
            names = "--id",
            description = "ID of the Policy to update",
            required = true
    )
    Long policyId;

    @Override
    public void run() {
        PolicyResource policy;
        try {
            policy = ObjectMapperUtils.stringToResource(
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
            ResponseEntity<ObjectNode> policyResponseEntity =
                    policyUpdateCommand.policyCommands.getPolicyClient().updatePolicyResponseEntity(policyId, policy);
            if(policyResponseEntity.getStatusCode().equals(HttpStatus.OK)) {
                policy = ObjectMapperUtils.convertObjectNode(policyResponseEntity.getBody(), PolicyResource.class);
                System.out.println("Policy UPDATED:\n" + ObjectMapperUtils.formatAsString(policy));
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
