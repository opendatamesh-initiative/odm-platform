package org.opendatamesh.platform.up.policy.server.exceptions;

import org.opendatamesh.platform.up.policy.api.v1.errors.PolicyserviceOpaAPIStandardError;
import org.springframework.http.HttpStatus;

/**
 * Http Status: 404
 * 
 * The requested resource could not be found but may be available in the future. 
 * Subsequent requests by the client are permissible.
 */
public class NotFoundException extends PolicyserviceOpaAPIException {

	public NotFoundException(PolicyserviceOpaAPIStandardError error, String message) {
		super(error, message);
	}

	public NotFoundException() {
		super();
	}

	public NotFoundException(PolicyserviceOpaAPIStandardError error, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(error, message, cause, enableSuppression, writableStackTrace);
	}


	public NotFoundException(PolicyserviceOpaAPIStandardError error, String message, Throwable cause) {
		super(error, message, cause);
	}


	public NotFoundException(Throwable cause) {
		super(cause);
	}


	@Override
	public HttpStatus getStatus() {
		return HttpStatus.NOT_FOUND;
	}

}