package org.opendatamesh.odm.cli.commands.policy.get;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.odm.cli.utils.ObjectMapperUtils;
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
    private PolicyGetCommand policyGetCommand;

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

    @Override
    public void run() {

        try {
            // Remove ResponseEntity and change the methods used by the client after refactoring RestUtils in policy service
            ResponseEntity<ObjectNode> policyObjectNodeResponseEntity;
            if(!rootIdFlag)
                policyObjectNodeResponseEntity = policyGetCommand.policyCommands.getPolicyClient().readOnePolicyVersionResponseEntity(policyId);
            else
                policyObjectNodeResponseEntity = policyGetCommand.policyCommands.getPolicyClient().readOnePolicyResponseEntity(policyId);
            if(policyObjectNodeResponseEntity.getStatusCode().equals(HttpStatus.OK)) {
                PolicyResource policyResource = ObjectMapperUtils.convertObjectNode(
                        policyObjectNodeResponseEntity.getBody(), PolicyResource.class
                );
                System.out.println(ObjectMapperUtils.formatAsString(policyResource));
            }
            else if(policyObjectNodeResponseEntity.getStatusCode().equals(HttpStatus.NOT_FOUND))
                System.out.println("Policy with " + (rootIdFlag ? "root" : "") + "ID [" + policyId + "] not found");
            else
                System.out.println("Got an unexpected response. Error code: " + policyObjectNodeResponseEntity.getStatusCode());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
