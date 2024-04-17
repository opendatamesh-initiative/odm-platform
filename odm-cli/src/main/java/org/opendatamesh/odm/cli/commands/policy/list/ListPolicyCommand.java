package org.opendatamesh.odm.cli.commands.policy.list;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.odm.cli.utils.ObjectMapperUtils;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

import java.util.List;

@Command(
        name = "policy",
        description = "Lists all available Policies",
        version = "odm-cli policy list policy 1.0.0",
        mixinStandardHelpOptions = true
)
public class ListPolicyCommand implements Runnable {
    
    @ParentCommand
    private PolicyListCommand policyListCommand;

    @Override
    public void run() {
        try {
            ResponseEntity<ObjectNode> policyResourceResponseEntity =
                    policyListCommand.policyCommands.getPolicyClient().readAllPoliciesResponseEntity();
            if(policyResourceResponseEntity.getStatusCode().equals(HttpStatus.OK)) {
                List<PolicyResource> policies = ObjectMapperUtils.extractListFromPageFromObjectNode(
                        policyResourceResponseEntity.getBody(), PolicyResource.class
                );
                if (policies.size() == 0)
                    System.out.println("[]");
                for (PolicyResource policy : policies)
                    System.out.println(ObjectMapperUtils.formatAsString(policy));
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
