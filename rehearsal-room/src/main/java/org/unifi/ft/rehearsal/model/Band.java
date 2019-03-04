package org.unifi.ft.rehearsal.model;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class Band {

	private String username;
	private String passw;
	private List<Instruments> instruments;
	
	public Band(String username, String passw, List<Instruments> instruments) {
		this.username = username;
		this.passw = passw;
		this.instruments = instruments;
	}

	public String getUsername() {
		return username;
	}

	public String getPassw() {
		return passw;
	}

	public List<Instruments> getInstruments() {
		return instruments;
	}

	public void setInstruments(List<Instruments> instruments) {
		this.instruments = instruments;
	}

}
