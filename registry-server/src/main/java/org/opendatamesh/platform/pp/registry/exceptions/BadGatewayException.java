package org.opendatamesh.platform.pp.registry.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Http Status: 502
 * 
 * The 502 (Bad Gateway) status code indicates that the server, while acting as a gateway 
 * or proxy, received an invalid response from an inbound server it accessed while 
 * attempting to fulfill the request.
 * 
 * EXAMPLE: An internal call to a service exposed by the utility plane fails
 * 
 * @see https://www.rfc-editor.org/rfc/rfc9110.html#name-502-bad-gateway
 * @see OpenDataMeshAPIStandardError
 * @see OpenDataMeshAPIExceptionHandler
 */
public class BadGatewayException extends OpenDataMeshAPIException {

	public BadGatewayException(OpenDataMeshAPIStandardError error, String message) {
		super(error, message);
	}

	
	public BadGatewayException() {
		super();
	}


	public BadGatewayException(OpenDataMeshAPIStandardError error, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(error, message, cause, enableSuppression, writableStackTrace);
	}


	public BadGatewayException(OpenDataMeshAPIStandardError error, String message, Throwable cause) {
		super(error, message, cause);
	}


	public BadGatewayException(Throwable cause) {
		super(cause);
	}


	@Override
	public HttpStatus getStatus() {
		return HttpStatus.BAD_GATEWAY;
	}

}