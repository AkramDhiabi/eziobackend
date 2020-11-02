package com.gemalto.eziomobile.demo.exception;

public class DataParseException extends RuntimeException {

	private static final long serialVersionUID = 4409435602593682277L;
	
	public DataParseException() {
		super();
	}

	public DataParseException(String message) {
		super(message);
	}

	public DataParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public DataParseException(Throwable cause) {
		super(cause);
	}

	protected DataParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}


}
