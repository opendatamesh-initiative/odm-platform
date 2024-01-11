package org.opendatamesh.platform.core.commons.clients.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiStandardErrors;

import java.util.Date;

@Data
public class ErrorRes {
	
	// HTTP Status code
	@Schema(description = "HTTP numeric status code")
	int status;

	// Standard error code
	@Schema(description = "HTTP text status code")
	String code;

	// Standard error description
	@Schema(description = "Error description")
	String description;

	// Exception message. 
	// Do not include exception cause's message. 
	// It is appended only to the log error message.
	@Schema(description = "Error message")
	String message;

	// Service endpoint
	String path;

	// Error timestamp
	@Schema(description = "Error timestamp")
	long timestamp = new Date().getTime();

	public ErrorRes() {

	}

	public ErrorRes(int status, ODMApiStandardErrors error, String message, String path) {
		super();
		this.status = status;
		this.code = error.code();
		this.description = error.description();
		this.message = message;
		this.path = path;
	}	

	public ErrorRes(int status, String errorCode, String errorDescription,  String message, String path) {
		super();
		this.status = status;
		this.code = errorCode;
		this.description = errorDescription;
		this.message = message;
		this.path = path;
	}	
}