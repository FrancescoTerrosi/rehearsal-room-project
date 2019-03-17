package org.unifi.ft.rehearsal.exceptions;

public class PasswordNotMatchingException extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;

	public PasswordNotMatchingException() {
		super();
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
