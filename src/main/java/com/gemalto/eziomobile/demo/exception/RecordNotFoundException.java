package com.gemalto.eziomobile.demo.exception;

public class RecordNotFoundException extends Exception {

	private static final long serialVersionUID = -8103172450816804974L;

	public RecordNotFoundException() {
		super();
	}

	public RecordNotFoundException(String message) {
		super(message);
	}

	public RecordNotFoundException(Throwable cause) {
		super(cause);
	}

	public RecordNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	protected RecordNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
