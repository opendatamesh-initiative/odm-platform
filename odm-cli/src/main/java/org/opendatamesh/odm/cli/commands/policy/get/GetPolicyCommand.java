package org.opendatamesh.odm.cli.commands.policy.get;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

@Command(
        name = "policy",
        description = "Get a specific Policy given its root ID or the version ID",
        version = "odm-cli registry get policy 1.0.0",
        mixinStandardHelpOptions = true
)
public class GetPolicyCommand implements Runnable {

    @ParentCommand
    private GetCommand getCommand;

    @Option(
            names = "--id",
            description = "ID of the Policy",
            required = true
    )
    Long policyId;

    @Option(
            names = "--root",
            description = "Whether the ID is the root ID (default) or the version ID. Set it to 'false' to get by version ID",
            defaultValue = "true"
    )
    Boolean rootIdFlag;

    ObjectMapper objectMapper = ObjectMapperFactory.JSON_MAPPER;

    @Override
    public void run() {

        try {
            // Remove ResponseEntity and change the methods used by the client after refactoring RestUtils in policy service
            ResponseEntity<PolicyResource> policyResponseEntity;
            if(!rootIdFlag)
                policyResponseEntity = getCommand.policyCommands.getPolicyClient().readOnePolicyVersionResponseEntity(policyId);
            else
                policyResponseEntity = getCommand.policyCommands.getPolicyClient().readOnePolicyResponseEntity(policyId);

            if(policyResponseEntity.getStatusCode().equals(HttpStatus.OK)) {
                PolicyResource policyResource = policyResponseEntity.getBody();
                System.out.println(objectMapper.writeValueAsString(policyResource));
            }
            else if(policyResponseEntity.getStatusCode().equals(HttpStatus.NOT_FOUND))
                System.out.println("Policy with " + (rootIdFlag ? "root" : "") + "ID [" + policyId + "] not found");
            else
                System.out.println("Got an unexpected response. Error code: " + policyResponseEntity.getStatusCode());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
