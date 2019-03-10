package org.unifi.ft.rehearsal.exceptions;

public class PasswordNotMatchingException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public PasswordNotMatchingException() {
		super();
	}

	public PasswordNotMatchingException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public PasswordNotMatchingException(String message, Throwable cause) {
		super(message, cause);
	}

	public PasswordNotMatchingException(Throwable cause) {
		super(cause);
	}
	
	public PasswordNotMatchingException(String message) {
		super(message);
	}

}
