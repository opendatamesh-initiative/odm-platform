package org.opendatamesh.platform.up.policy.api.v1.clients;

import org.opendatamesh.platform.up.policy.api.v1.resources.DocumentResource;
import org.opendatamesh.platform.up.policy.api.v1.resources.EvaluationResource;

import java.util.Random;

public class PolicyEngineClientMock implements PolicyEngineClient{

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random random = new Random();

    public EvaluationResource evaluateDocument(DocumentResource documentResource) {
        EvaluationResource response = new EvaluationResource();
        response.setPolicyEvaluationId(documentResource.getPolicyEvaluationId());
        response.setEvaluationResult(random.nextBoolean());
        String output = String.valueOf(random.nextInt());
        StringBuilder sb = new StringBuilder(32);
        for(int i=0; i<32; i++)
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        response.setOutputObject(output);
        return response;
    }

}
