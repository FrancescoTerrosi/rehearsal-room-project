package org.unifi.ft.rehearsal.exceptions;

import org.unifi.ft.rehearsal.annotations.Generated;

@Generated("TimeException")
public class InvalidTimeException extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;
	
	public InvalidTimeException() {
		super();
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
