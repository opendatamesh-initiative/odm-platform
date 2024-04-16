package org.opendatamesh.odm.cli.commands.policy.update;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.odm.cli.utils.FileReaderUtils;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

import java.io.IOException;

@Command(
        name = "result",
        description = "Update a Policy Evaluation Result",
        version = "odm-cli policy update result 1.0.0",
        mixinStandardHelpOptions = true
)
public class UpdatePolicyEvaluationResultCommand implements Runnable {

    @ParentCommand
    private PolicyUpdateCommand policyUpdateCommand;

    private static final ObjectMapper objectMapper = ObjectMapperFactory.JSON_MAPPER;

    @Option(
            names = "--result-file",
            description = "Path of the JSON descriptor of the Policy Evaluation Result object",
            required = true
    )
    String policyEvaluationResultDescriptorPath;

    @Option(
            names = "--id",
            description = "ID of the Policy Evaluation Result to update",
            required = true
    )
    Long policyEvaluationResultId;

    @Override
    public void run() {
        PolicyEvaluationResultResource policyEvaluationResult;
        try {
            policyEvaluationResult = objectMapper.convertValue(
                    FileReaderUtils.readFileFromPath(policyEvaluationResultDescriptorPath),
                    PolicyEvaluationResultResource.class
            );
        } catch (IOException e) {
            System.out.println(
                    "Impossible to read file [" + policyEvaluationResultDescriptorPath + "]. Check if the file exists and retry"
            );
            return;
        }
        try {
            ResponseEntity<PolicyEvaluationResultResource> policyEvaluationResultResponseEntity =
                    policyUpdateCommand.policyCommands.getPolicyClient().updatePolicyEvaluationResultResponseEntity(
                            policyEvaluationResultId, policyEvaluationResult
                    );
            if(policyEvaluationResultResponseEntity.getStatusCode().equals(HttpStatus.OK)) {
                policyEvaluationResult = policyEvaluationResultResponseEntity.getBody();
                System.out.println("Policy Evaluation Result UPDATED:\n" + objectMapper.writeValueAsString(policyEvaluationResult));
            }
            else
                System.out.println("Got an unexpected response. Error code: " + policyEvaluationResultResponseEntity.getStatusCode());
        } catch (ResourceAccessException e) {
            System.out.println("Impossible to connect with Policy server. Verify the URL and retry");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}
