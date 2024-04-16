package org.opendatamesh.odm.cli.commands.policy.validate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.odm.cli.utils.FileReaderUtils;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationRequestResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

import java.io.IOException;

@Command(
        name = "document",
        description = "Validate a document",
        version = "odm-cli policy validate document 1.0.0",
        mixinStandardHelpOptions = true
)
public class ValidateDocumentCommand implements Runnable {

    @ParentCommand
    private PolicyValidateCommand policyValidateCommand;

    private static final ObjectMapper objectMapper = ObjectMapperFactory.JSON_MAPPER;

    @Option(
            names = "--document-file",
            description = "Path of the JSON descriptor of the PolicyEvaluationRequestResource to evaluate",
            required = true
    )
    String evaluationRequestPath;

    @Override
    public void run() {
        PolicyEvaluationRequestResource evaluationRequest;
        try {
            evaluationRequest = objectMapper.convertValue(
                    FileReaderUtils.readFileFromPath(evaluationRequestPath),
                    PolicyEvaluationRequestResource.class
            );
        } catch (IOException e) {
            System.out.println(
                    "Impossible to read file [" + evaluationRequestPath + "]. Check if the file exists and retry"
            );
            return;
        }
        try {
            ResponseEntity<PolicyEvaluationResultResource> validateResponseEntity =
                    policyValidateCommand.policyCommands.getPolicyClient().validateInputObjectResponseEntity(evaluationRequest);
            if(validateResponseEntity.getStatusCode().equals(HttpStatus.OK)) {
                PolicyEvaluationResultResource evaluationResult= validateResponseEntity.getBody();
                System.out.println("Policy Engine CREATED:\n" + objectMapper.writeValueAsString(evaluationResult));
            }
            else
                System.out.println("Got an unexpected response. Error code: " + validateResponseEntity.getStatusCode());
        } catch (ResourceAccessException e) {
            System.out.println("Impossible to connect with Policy server. Verify the URL and retry");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
