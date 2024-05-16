package org.opendatamesh.platform.core.commons.servers.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Http Status: 500
 * 
 * The 500 (Internal Server Error) status code indicates that the server encountered an 
 * unexpected condition that prevented it from fulfilling the request.
 * 
 * EXAMPLE: An error occurred in the backend database
 * 
 * @see https://www.rfc-editor.org/rfc/rfc9110.html#name-500-internal-server-error
 * @see ODMDevOpsAPIStandardError
 * @see ODMApiExceptionHandler
 */
public class InternalServerException extends ODMApiException {
	public InternalServerException() {
	}

	public InternalServerException(String message) {
		super(message);
	}

	public InternalServerException(ODMApiStandardErrors error, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(error, message, cause, enableSuppression, writableStackTrace);
	}

	public InternalServerException(ODMApiStandardErrors error, String message, Throwable cause) {
		super(error, message, cause);
	}

	public InternalServerException(ODMApiStandardErrors error, String message) {
		super(error, message);
	}

	public InternalServerException(Throwable cause) {
		super(cause);
	}

	@Override
	public HttpStatus getStatus() {
		return HttpStatus.INTERNAL_SERVER_ERROR;
	}
}
