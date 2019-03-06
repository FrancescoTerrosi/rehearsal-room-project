package org.unifi.ft.rehearsal.exceptions;

public class InvalidTimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public InvalidTimeException() {
		super();
	}

	public InvalidTimeException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidTimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidTimeException(Throwable cause) {
		super(cause);
	}
	
	public InvalidTimeException(String message) {
		super(message);
	}

}
