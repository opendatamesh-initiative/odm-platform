package org.opendatamesh.odm.cli.commands.policy.get;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
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
    private GetCommand getCommand;

    @Option(
            names = "--id",
            description = "ID of the Policy",
            required = true
    )
    Long engineId;

    ObjectMapper objectMapper = ObjectMapperFactory.JSON_MAPPER;

    @Override
    public void run() {

        try {
            // Remove ResponseEntity and change the methods used by the client after refactoring RestUtils in policy service
            ResponseEntity<PolicyEngineResource> engineResponseEntity =
                    getCommand.policyCommands.getPolicyClient().readOnePolicyEngineResponseEntity(engineId);

            if(engineResponseEntity.getStatusCode().equals(HttpStatus.OK)) {
                PolicyEngineResource engineResource = engineResponseEntity.getBody();
                System.out.println(objectMapper.writeValueAsString(engineResource));
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
