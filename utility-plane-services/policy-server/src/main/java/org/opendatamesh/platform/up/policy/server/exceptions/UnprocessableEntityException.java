package org.opendatamesh.platform.up.policy.server.exceptions;

import org.opendatamesh.platform.up.policy.api.v1.errors.PolicyserviceOpaAPIStandardError;
import org.springframework.http.HttpStatus;

/**
 * Http Status: 422
 * 
 * The request was well-formed but was unable to be followed due to semantic errors.
 * 
 * EXAMPLE: The provided descriptor respects the DPDS but violates some global 
 * policies enforced by the policyService 
 */
public class UnprocessableEntityException extends PolicyserviceOpaAPIException {

	public UnprocessableEntityException(PolicyserviceOpaAPIStandardError error, String message) {
		super(error, message);
	}
	
	public UnprocessableEntityException() {
		super();
	}

	public UnprocessableEntityException(PolicyserviceOpaAPIStandardError error, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(error, message, cause, enableSuppression, writableStackTrace);
	}

	public UnprocessableEntityException(PolicyserviceOpaAPIStandardError error, String message, Throwable cause) {
		super(error, message, cause);
	}

	public UnprocessableEntityException(Throwable cause) {
		super(cause);
	}

	@Override
	public HttpStatus getStatus() {
		return HttpStatus.UNPROCESSABLE_ENTITY;
	}

}