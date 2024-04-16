package org.opendatamesh.odm.cli.commands.policy.list;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.policy.api.resources.PagedPolicyResource;
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

    private static final ObjectMapper objectMapper = ObjectMapperFactory.JSON_MAPPER;

    @Override
    public void run() {
        try {
            ResponseEntity<PagedPolicyResource> policyResourceResponseEntity =
                    policyListCommand.policyCommands.getPolicyClient().readAllPoliciesResponseEntity();
            if(policyResourceResponseEntity.getStatusCode().equals(HttpStatus.OK)) {
                List<PolicyResource> policies = policyResourceResponseEntity.getBody().getContent();
                if (policies.size() == 0)
                    System.out.println("[]");
                for (PolicyResource policy : policies)
                    System.out.println(objectMapper.writeValueAsString(policy));
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
