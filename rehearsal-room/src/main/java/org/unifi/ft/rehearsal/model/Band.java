package org.unifi.ft.rehearsal.model;

import org.springframework.stereotype.Component;

@Component
public class Band {

	private String username;
	private String passw;
	
	public Band(String username, String passw) {
		this.username = username;
		this.passw = passw;
	}

	public String getUsername() {
		return username;
	}

	public String getPassw() {
		return passw;
	}
	
}
