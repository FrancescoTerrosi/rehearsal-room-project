package org.unifi.ft.rehearsal.exceptions;

public class RoomNotFreeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public RoomNotFreeException() {
		super();
	}

	public RoomNotFreeException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public RoomNotFreeException(String message, Throwable cause) {
		super(message, cause);
	}

	public RoomNotFreeException(String message) {
		super(message);
	}

	public RoomNotFreeException(Throwable cause) {
		super(cause);
	}

}
