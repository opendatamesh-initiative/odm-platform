package org.opendatamesh.odm.cli.commands.policy.get;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.odm.cli.utils.ObjectMapperUtils;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

@Command(
        name = "result",
        description = "Get a specific Policy Evaluation Result given its ID",
        version = "odm-cli policy get result 1.0.0",
        mixinStandardHelpOptions = true
)
public class GetPolicyEvaluationResultCommand implements Runnable {
    
    @ParentCommand
    private PolicyGetCommand policyGetCommand;

    @Option(
            names = "--id",
            description = "ID of the Policy Evaluation Result",
            required = true
    )
    Long resultId;

    @Override
    public void run() {

        try {
            // Remove ResponseEntity and change the methods used by the client after refactoring RestUtils in policy service
            ResponseEntity<ObjectNode> resultResponseEntity =
                    policyGetCommand.policyCommands.getPolicyClient().readOnePolicyEvaluationResultResponseEntity(resultId);

            if(resultResponseEntity.getStatusCode().equals(HttpStatus.OK)) {
                PolicyEvaluationResultResource resultResource = ObjectMapperUtils.convertObjectNode(
                        resultResponseEntity.getBody(), PolicyEvaluationResultResource.class
                );
                System.out.println(ObjectMapperUtils.formatAsString(resultResource));
            }
            else if(resultResponseEntity.getStatusCode().equals(HttpStatus.NOT_FOUND))
                System.out.println("Policy Evaluation Result with ID [" + resultId + "] not found");
            else
                System.out.println("Got an unexpected response. Error code: " + resultResponseEntity.getStatusCode());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    
}
