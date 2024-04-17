package org.opendatamesh.odm.cli.commands.policy.update;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.odm.cli.utils.FileReaderUtils;
import org.opendatamesh.odm.cli.utils.ObjectMapperUtils;
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
        description = "Update a Policy Engine",
        version = "odm-cli policy update engine 1.0.0",
        mixinStandardHelpOptions = true
)
public class UpdatePolicyEngineCommand implements Runnable {

    @ParentCommand
    private PolicyUpdateCommand policyUpdateCommand;

    @Option(
            names = "--engine-file",
            description = "Path of the JSON descriptor of the Policy Engine object",
            required = true
    )
    String policyEngineDescriptorPath;

    @Option(
            names = "--id",
            description = "ID of the Policy Engine to update",
            required = true
    )
    Long policyEngineId;

    @Override
    public void run() {
        PolicyEngineResource policyEngine;
        try {
            policyEngine = ObjectMapperUtils.stringToResource(
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
            ResponseEntity<ObjectNode> engineResponseEntity =
                    policyUpdateCommand.policyCommands.getPolicyClient().updatePolicyEngineResponseEntity(policyEngineId, policyEngine);
            if(engineResponseEntity.getStatusCode().equals(HttpStatus.OK)) {
                policyEngine = ObjectMapperUtils.convertObjectNode(engineResponseEntity.getBody(), PolicyEngineResource.class);
                System.out.println("Policy Engine UPDATED:\n" + ObjectMapperUtils.formatAsString(policyEngine));
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
