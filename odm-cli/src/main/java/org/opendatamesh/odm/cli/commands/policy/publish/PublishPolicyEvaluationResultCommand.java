package org.opendatamesh.odm.cli.commands.policy.publish;

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
        description = "Publish a Policy Evaluation Result",
        version = "odm-cli policy publish result 1.0.0",
        mixinStandardHelpOptions = true
)
public class PublishPolicyEvaluationResultCommand implements Runnable {

    @ParentCommand
    private PolicyPublishCommand policyPublishCommand;

    private static final ObjectMapper objectMapper = ObjectMapperFactory.JSON_MAPPER;

    @Option(
            names = "--result-file",
            description = "Path of the JSON descriptor of the Policy Evaluation Result object",
            required = true
    )
    String policyEvaluationResultDescriptorPath;

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
            ResponseEntity<PolicyEvaluationResultResource> resultResponseEntity =
                    policyPublishCommand.policyCommands.getPolicyClient().createPolicyEvaluationResultResponseEntity(policyEvaluationResult);
            if(resultResponseEntity.getStatusCode().equals(HttpStatus.CREATED)) {
                policyEvaluationResult = resultResponseEntity.getBody();
                System.out.println("Policy Evaluation Result CREATED:\n" + objectMapper.writeValueAsString(policyEvaluationResult));
            }
            else
                System.out.println("Got an unexpected response. Error code: " + resultResponseEntity.getStatusCode());
        } catch (ResourceAccessException e) {
            System.out.println("Impossible to connect with Policy server. Verify the URL and retry");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
