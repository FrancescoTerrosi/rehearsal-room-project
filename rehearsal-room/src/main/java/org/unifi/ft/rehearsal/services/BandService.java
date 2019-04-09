package org.unifi.ft.rehearsal.services;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.unifi.ft.rehearsal.exceptions.UsernameAlreadyExistsException;
import org.unifi.ft.rehearsal.model.BandDetails;
import org.unifi.ft.rehearsal.exceptions.InvalidRegistrationField;
import org.unifi.ft.rehearsal.exceptions.PasswordNotMatchingException;
import org.unifi.ft.rehearsal.repository.mongo.IBandDetailsMongoRepository;

@Service("BandService")
public class BandService implements UserDetailsService {

	public static final String USER_NOT_FOUND = "There is no user with that name!";
	public static final String ALREADY_EXISTING_USERNAME = "There is already a user with that name!";
	public static final String PASSW_NOT_MATCHING = "The two passwords do not match!";
	public static final String EMPTY_FIELDS = "These fields can not be empty!";

	private static final Logger LOGGER = LogManager.getLogger(BandService.class);

	private IBandDetailsMongoRepository repository;

	private PasswordEncoder encoder;

	@Autowired
	public BandService(IBandDetailsMongoRepository repository, PasswordEncoder encoder) {
		this.repository = repository;
		this.encoder = encoder;
	}

	public BandDetails register(String name, String password, String confirmPassword) {
		checkValidParameters(name, password, confirmPassword);
		if (!existsUserByUsername(name)) {
			return handleRegistration(name, password, confirmPassword);
		} else {
			LOGGER.warn(name + " - "+ALREADY_EXISTING_USERNAME);
			throw new UsernameAlreadyExistsException(ALREADY_EXISTING_USERNAME);
		}
	}

	private void checkValidParameters(String name, String password, String confirmPassword) {
		if ((name.equals("") || name.contains(" ")) || (password.equals("") || password.contains(" ")) || (confirmPassword.equals("") || confirmPassword.contains(" "))) {
			throw new InvalidRegistrationField(EMPTY_FIELDS);
		}
	}

	private BandDetails handleRegistration(String name, String password, String confirmPassword) {
		if (password.equals(confirmPassword)) {
			BandDetails user = repository.save(createUser(name, password));
			LOGGER.info("User " + name + " just joined our system!");
			return user;
		} else {
			LOGGER.warn(name + " - "+PASSW_NOT_MATCHING);
			throw new PasswordNotMatchingException(PASSW_NOT_MATCHING);
		}
	}

	@Override
	public BandDetails loadUserByUsername(String username) {
		List<BandDetails> temp = repository.findAll();
		for (BandDetails user : temp) {
			if (user.getUsername().equals(username)) {
				return user;
			}
		}
		LOGGER.warn(username + " - "+USER_NOT_FOUND);
		throw new UsernameNotFoundException(USER_NOT_FOUND);
	}

	public boolean existsUserByUsername(String username) {
		List<BandDetails> temp = repository.findAll();
		for (BandDetails user : temp) {
			if (user.getUsername().equals(username)) {
				return true;
			}
		}
		return false;
	}

	private BandDetails createUser(String name, String password) {
		String[] authorities = { "USER" };
		return new BandDetails(name, encoder.encode(password), authorities);
	}

}
