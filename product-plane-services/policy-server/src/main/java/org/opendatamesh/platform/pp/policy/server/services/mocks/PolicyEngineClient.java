package org.opendatamesh.platform.pp.policy.server.services.mocks;

import java.util.Random;

public class PolicyEngineClient {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random random = new Random();

    private String serverAddress;

    public PolicyEngineClient(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public PolicyEngineValidationResponse validatePolicy(PolicyEngineValidationRequest request) {
        PolicyEngineValidationResponse response = new PolicyEngineValidationResponse();
        response.setResult(random.nextBoolean());
        String output = String.valueOf(random.nextInt());
        StringBuilder sb = new StringBuilder(32);
        for(int i=0; i<32; i++)
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        response.setOutputObject(output);
        return response;
    }

}
