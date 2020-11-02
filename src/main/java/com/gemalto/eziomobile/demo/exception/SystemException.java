package com.gemalto.eziomobile.demo.exception;

import com.gemalto.eziomobile.demo.logger.LoggerUtil;

public class SystemException extends RuntimeException {

	private static final long serialVersionUID = -6966088779330495149L;
	private static final LoggerUtil logger = new LoggerUtil(SystemException.class);
	
	private Exception originalException;

	/**
	 * the error object that contains the error details.
	 */
	private ErrorInfo systemErrorInfo;

	/**
	 * This method creates a new exception object with the error object passed.
	 * @param error error object
	 */
	
	
	public SystemException(String message,Exception e){		
		logger.error(message, e);
		this.originalException=e;
	
	}
	
	public SystemException(String message,Exception e,ErrorInfo error){		
		super();
		systemErrorInfo = error;
		systemErrorInfo.setErrorDesc(message);
		this.originalException=e;
		logger.error(message, e);
	
	}
	
	/**
	 * @param string
	 */
	public SystemException(String message) {
		logger.error(message);
	}


	/**
	 * this method returns the error object.
	 * @return error object
	 */
	public ErrorInfo getErrorInfo() {
		return systemErrorInfo;
	}

	/**
	 * this method sets the error object.
	 * @param cpsError error object.
	 */
	public void setErrorInfo(ErrorInfo cpsError) {
		this.systemErrorInfo = cpsError;
	}

	/**
	 * @return the originalException
	 */
	public Exception getOriginalException() {
		return originalException;
	}

	/**
	 * @param originalException the originalException to set
	 */
	public void setOriginalException(Exception originalException) {
		this.originalException = originalException;
	}

}
