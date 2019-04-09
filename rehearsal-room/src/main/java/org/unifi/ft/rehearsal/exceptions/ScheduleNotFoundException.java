package org.unifi.ft.rehearsal.exceptions;

import org.unifi.ft.rehearsal.annotations.Generated;

@Generated("ScheduleException")
public class ScheduleNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ScheduleNotFoundException() {
		super();
	}

	public ScheduleNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ScheduleNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public ScheduleNotFoundException(String message) {
		super(message);
	}

	public ScheduleNotFoundException(Throwable cause) {
		super(cause);
	}

		
	
}
