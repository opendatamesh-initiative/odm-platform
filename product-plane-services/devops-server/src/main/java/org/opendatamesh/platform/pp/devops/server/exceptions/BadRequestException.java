package org.opendatamesh.platform.pp.devops.server.exceptions;

import org.opendatamesh.platform.pp.devops.api.resources.ODMDevOpsAPIStandardError;
import org.springframework.http.HttpStatus;

/**
 * Http Status: 400
 * 
 * The 400 (Bad Request) status code indicates that the server cannot or will
 * not process
 * the request due to something that is perceived to be a client error
 * (e.g., malformed request syntax, invalid request message framing, or
 * deceptive
 * request routing).
 * 
 * EXAMPLE: a request missing some mandatory parameters
 * 
 * @see https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request
 * @see ODMDevOpsAPIStandardError
 * @see OpenDataMeshAPIExceptionHandler
 */
public class BadRequestException extends OpenDataMeshAPIException {

	public BadRequestException() {
		super();
	}

	public BadRequestException(ODMDevOpsAPIStandardError error, String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(error, message, cause, enableSuppression, writableStackTrace);
	}

	public BadRequestException(ODMDevOpsAPIStandardError error, String message, Throwable cause) {
		super(error, message, cause);
	}

	public BadRequestException(ODMDevOpsAPIStandardError error, String message) {
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