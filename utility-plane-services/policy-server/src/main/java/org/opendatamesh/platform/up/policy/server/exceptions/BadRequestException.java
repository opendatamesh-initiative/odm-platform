package org.opendatamesh.platform.up.policy.server.exceptions;

import org.opendatamesh.platform.up.policy.api.v1.errors.PolicyserviceOpaAPIStandardError;
import org.springframework.http.HttpStatus;

public class BadRequestException extends PolicyserviceOpaAPIException {

	public BadRequestException() {
		super();
	}

	public BadRequestException(PolicyserviceOpaAPIStandardError error, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(error, message, cause, enableSuppression, writableStackTrace);
	}

	public BadRequestException(PolicyserviceOpaAPIStandardError error, String message, Throwable cause) {
		super(error, message, cause);
	}

	public BadRequestException(PolicyserviceOpaAPIStandardError error, String message) {
		super(error, message);
	}

	public BadRequestException(Throwable cause) {
		super(cause);
	}

	@Override
	public HttpStatus getStatus() {
		return HttpStatus.BAD_REQUEST;
	}

}