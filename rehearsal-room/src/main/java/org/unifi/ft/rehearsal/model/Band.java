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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((passw == null) ? 0 : passw.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Band other = (Band) obj;
		if (passw == null) {
			if (other.passw != null)
				return false;
		} else if (!passw.equals(other.passw))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
	
}
