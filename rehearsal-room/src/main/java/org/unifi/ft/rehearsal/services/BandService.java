package org.unifi.ft.rehearsal.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.unifi.ft.rehearsal.model.BandDetails;
import org.unifi.ft.rehearsal.mongo.IBandDetailsMongoRepository;

@Service("BandService")
public class BandService {
	
	private IBandDetailsMongoRepository repository;

	private PasswordEncoder encoder;
	
	@Autowired
	public BandService(IBandDetailsMongoRepository repository, PasswordEncoder encoder) {
		this.repository = repository;
		this.encoder = encoder;
	}
	
	public UserDetails register(String name, String password, String confirmPassword) {
		if (!existsUserByUsername(name)) {
			return handleRegistration(name, password, confirmPassword);
		} else {
			throw new RuntimeException();
		}
	}

	private UserDetails handleRegistration(String name, String password, String confirmPassword) {
		if (password.equals(confirmPassword)) {
			UserDetails user = createUser(name, password);
			repository.save(user);
			return user;
		} else {
			throw new RuntimeException();
		}
	}

	public UserDetails loadUserByUsername(String username) {
		List<UserDetails> temp = repository.findAll();
		for (UserDetails user : temp) {
			if (user.getUsername().equals(username)) {
				return user;
			}
		}
		throw new UsernameNotFoundException("ERROR!!!111!!!!!111");
	}

	public boolean existsUserByUsername(String username) {
		List<UserDetails> temp = repository.findAll();
		for (UserDetails user : temp) {
			if (user.getUsername().equals(username)) {
				return true;
			}
		}
		return false;
	}

	private UserDetails createUser(String name, String password) {
		String[] authorities = { "USER" };
		return new BandDetails(name, encoder.encode(password), authorities);
	}

}
