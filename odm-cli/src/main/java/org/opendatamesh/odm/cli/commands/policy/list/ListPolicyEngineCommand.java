package org.opendatamesh.odm.cli.commands.policy.list;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.odm.cli.utils.JsonUtils;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEngineResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

import java.util.List;

@Command(
        name = "engine",
        description = "Lists all available Policy Engines",
        version = "odm-cli policy list engine 1.0.0",
        mixinStandardHelpOptions = true
)
public class ListPolicyEngineCommand implements Runnable {
    
    @ParentCommand
    private PolicyListCommand policyListCommand;

    private static final ObjectMapper objectMapper = ObjectMapperFactory.JSON_MAPPER;

    @Override
    public void run() {
        try {
            ResponseEntity<ObjectNode> engineResourceResponseEntity =
                    policyListCommand.policyCommands.getPolicyClient().readAllPolicyEnginesResponseEntity();
            if(engineResourceResponseEntity.getStatusCode().equals(HttpStatus.OK)) {
                List<PolicyEngineResource> engines = JsonUtils.extractListFromPageFromObjectNode(
                        engineResourceResponseEntity.getBody(), PolicyEngineResource.class
                );
                if (engines.size() == 0)
                    System.out.println("[]");
                for (PolicyEngineResource engine : engines)
                    System.out.println(objectMapper.writeValueAsString(engine));
            }
            else
                System.out.println("Error in response from Policy Server");
        } catch (ResourceAccessException e) {
            System.out.println("Impossible to connect with Policy server. Verify the URL and retry");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}
