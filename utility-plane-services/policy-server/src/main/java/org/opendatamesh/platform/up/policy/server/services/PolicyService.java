package org.opendatamesh.platform.up.policy.server.services;

import org.opendatamesh.platform.up.policy.api.v1.errors.PolicyserviceOpaAPIStandardError;
import org.opendatamesh.platform.up.policy.server.exceptions.BadRequestException;
import org.opendatamesh.platform.up.policy.server.exceptions.UnprocessableEntityException;
import org.springframework.stereotype.Service;

@Service
public class PolicyService {

    public String getIdFromPolicy(String rawPolicy) {
        try {
            String rawPolicyIdLine =  rawPolicy.split("\n")[0];
            if (rawPolicyIdLine.contains("package")) {
                String rawPolicyId = rawPolicyIdLine.replace("package", "").trim();
                if (!(rawPolicyId == null) && !rawPolicyId.isEmpty()) {
                    return rawPolicyId;
                } else {
                    throw new BadRequestException(
                            PolicyserviceOpaAPIStandardError.SC400_ID_IS_EMPTY,
                            "ID cannot be empty, give a name to the package."
                    );
                }
            } else {
                throw new BadRequestException(
                        PolicyserviceOpaAPIStandardError.SC400_ID_IS_MISSING,
                        "Missing ID as first row of policy. Check syntax."
                );
            }
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new UnprocessableEntityException(
                    PolicyserviceOpaAPIStandardError.SC422_POLICY_SYNTAX_IS_INVALID,
                    "Error extracting ID from policy. Check syntax."
            );
        }

    }

}
