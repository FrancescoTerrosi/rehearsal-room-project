package org.unifi.ft.rehearsal.model;

import java.math.BigInteger;
import java.util.Collection;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

@Document("BandDetails")
public class BandDetails implements UserDetails {

	@Id
	private BigInteger id;
	private static final long serialVersionUID = 1L;
	private String username;
	private String passw;
	private String[] authorities;
	
	public BandDetails(String username, String passw, String... authorities) {
		this.username = username;
		this.passw = passw;
		this.authorities = authorities;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return passw;
	}
	
	public BigInteger getId() {
		return this.id;
	}

	public Collection<? extends GrantedAuthority> getAuthorities() {
		return AuthorityUtils.createAuthorityList(authorities);
	}

	public boolean isAccountNonExpired() {
		return true;
	}

	public boolean isAccountNonLocked() {
		return true;
	}

	public boolean isCredentialsNonExpired() {
		return true;
	}

	public boolean isEnabled() {
		return true;
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
		BandDetails other = (BandDetails) obj;
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
