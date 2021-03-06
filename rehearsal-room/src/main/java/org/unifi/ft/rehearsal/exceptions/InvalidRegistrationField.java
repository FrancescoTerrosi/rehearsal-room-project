package org.unifi.ft.rehearsal.exceptions;

import org.unifi.ft.rehearsal.annotations.Generated;

@Generated("RegistrationException")
public class InvalidRegistrationField extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;

	public InvalidRegistrationField() {
		super();
	}

	public InvalidRegistrationField(String s) {
		super(s);
	}

	public InvalidRegistrationField(Throwable cause) {
		super(cause);
	}

	public InvalidRegistrationField(String message, Throwable cause) {
		super(message, cause);
	}

}
