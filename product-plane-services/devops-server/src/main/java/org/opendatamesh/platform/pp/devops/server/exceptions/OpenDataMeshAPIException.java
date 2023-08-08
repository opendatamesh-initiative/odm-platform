package org.opendatamesh.platform.pp.devops.server.exceptions;

import org.opendatamesh.platform.pp.devops.api.resources.ODMDevOpsAPIStandardError;
import org.springframework.http.HttpStatus;

public abstract class OpenDataMeshAPIException extends RuntimeException{

	ODMDevOpsAPIStandardError error;

	public OpenDataMeshAPIException() {
		super();
	}

	public OpenDataMeshAPIException(ODMDevOpsAPIStandardError error, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		setError(error);
	}

	public OpenDataMeshAPIException(ODMDevOpsAPIStandardError error, String message,Throwable cause) {
		super(message, cause);
		setError(error);
	}

	public OpenDataMeshAPIException(ODMDevOpsAPIStandardError error, String message) {
		super(message);
		setError(error);
	}

	public OpenDataMeshAPIException(Throwable cause) {
		super(cause);
	}

	public void setError(ODMDevOpsAPIStandardError error) {
		this.error = error;
	}

	/**
	 * @return the error
	 */
	public ODMDevOpsAPIStandardError getStandardError() {
		return error;
	}

	/**
	 * @return the error code
	 */
	public String getStandardErrorCode() {
		return error!=null?error.code():null;		
	}

	/**
	 * @return the error description
	 */
	public String getStandardErrorDescription() {
		return error!=null?error.description():null;	
	}

	

	/**
	 * @return the errorName
	 */
	public String getErrorName() {
		return getClass().getSimpleName();	
	}

	/**
	 * @return the status
	 */
	public abstract HttpStatus getStatus();	
	

}