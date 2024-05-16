package org.opendatamesh.odm.cli.commands.policy.get;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.odm.cli.utils.ObjectMapperUtils;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEngineResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

@Command(
        name = "engine",
        description = "Get a specific Policy Engine given its ID",
        version = "odm-cli policy get engine 1.0.0",
        mixinStandardHelpOptions = true
)
public class GetPolicyEngineCommand implements Runnable {
    
    @ParentCommand
    private PolicyGetCommand policyGetCommand;

    @Option(
            names = "--id",
            description = "ID of the Policy Engine",
            required = true
    )
    Long engineId;

    @Override
    public void run() {

        try {
            // Remove ResponseEntity and change the methods used by the client after refactoring RestUtils in policy service
            ResponseEntity<ObjectNode> engineResponseEntity =
                    policyGetCommand.policyCommands.getPolicyClient().readOnePolicyEngineResponseEntity(engineId);
            if(engineResponseEntity.getStatusCode().equals(HttpStatus.OK)) {
                PolicyEngineResource engineResource = ObjectMapperUtils.convertObjectNode(
                        engineResponseEntity.getBody(), PolicyEngineResource.class
                );
                System.out.println(ObjectMapperUtils.formatAsString(engineResource));
            }
            else if(engineResponseEntity.getStatusCode().equals(HttpStatus.NOT_FOUND))
                System.out.println("Policy Engine with ID [" + engineId + "] not found");
            else
                System.out.println("Got an unexpected response. Error code: " + engineResponseEntity.getStatusCode());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    
}
