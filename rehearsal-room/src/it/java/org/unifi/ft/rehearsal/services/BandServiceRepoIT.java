package org.unifi.ft.rehearsal.services;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.unifi.ft.rehearsal.exceptions.InvalidRegistrationField;
import org.unifi.ft.rehearsal.exceptions.PasswordNotMatchingException;
import org.unifi.ft.rehearsal.exceptions.UsernameAlreadyExistsException;
import org.unifi.ft.rehearsal.model.BandDetails;
import org.unifi.ft.rehearsal.repository.mongo.IBandDetailsMongoRepository;

@SpringBootTest
@RunWith(SpringRunner.class)
public class BandServiceRepoIT {

	private BandService service;

	@Autowired
	private IBandDetailsMongoRepository repository;

	@Autowired
	private BCryptPasswordEncoder encoder;

	@Before
	public void setup() {
		repository.deleteAll();
		service = new BandService(repository, encoder);
	}

	@After
	public void cleanRepository() {
		repository.deleteAll();
	}

	@Test
	public void testValidRegistration() {
		UserDetails c = service.register("bandName", "bandPassword", "bandPassword");
		assertEquals(1, repository.count());
		UserDetails result = repository.findAll().get(0);
		assertEquals(c.getUsername(), result.getUsername());
		assertEquals(c.getPassword(), result.getPassword());
	}

	@Test(expected = UsernameAlreadyExistsException.class)
	public void testInvalidRegistrationUsernameAlreadyExists() {
		BandDetails toStore = createUser("bandName", "bandPassword");
		repository.save(toStore);
		service.register("bandName", "bandPassword","bandPassword");
	}
	
	@Test(expected=PasswordNotMatchingException.class)
	public void testInvalidRegistrationPasswordsNotMatching() {
		service.register("bandName", "bandPassword", "errorPassword");
	}
	
	@Test(expected = InvalidRegistrationField.class)
	public void testInvalidRegistrationEmptyUsername() {
		service.register("", "password", "password");
	}

	@Test(expected = InvalidRegistrationField.class)
	public void testInvalidRegistrationEmptyPassword() {
		service.register("bandName", "", "password");
	}
	
	@Test(expected = InvalidRegistrationField.class)
	public void testInvalidRegistrationEmptyConfirmPassword() {
		service.register("bandName", "password", "");
	}
	
	@Test(expected = InvalidRegistrationField.class)
	public void testInvalidRegistrationUsernameWithSpaces() {
		service.register("u s e r n a m e", "password", "password");
	}

	@Test(expected = InvalidRegistrationField.class)
	public void testInvalidRegistrationPasswordWithSpaces() {
		service.register("bandName", " ", " ");
	}

	@Test
	public void testLoadByUsername() {
		BandDetails c1 = createUser("bandName1", "bandPassword");
		BandDetails c2 = createUser("bandName2", "bandPassword");
		repository.save(c1);
		repository.save(c2);
		UserDetails result = service.loadUserByUsername("bandName1");
		assertThat(result, instanceOf(BandDetails.class));
		assertClient(c1, result);
	}

	@Test(expected = UsernameNotFoundException.class)
	public void testLoadOneByUsernameWhenThereIsNot() {
		BandDetails c1 = createUser("bandName1", "bandPassword");
		BandDetails c2 = createUser("bandName2", "bandPassword");
		repository.save(c1);
		repository.save(c2);
		UserDetails result = service.loadUserByUsername("error");
		assertNull(result);
	}
	
	@Test
	public void testExistsUserByUsername() {
		repository.save(createUser("bandName","bandPassword"));
		assertTrue(service.existsUserByUsername("bandName"));
	}
	
	@Test
	public void testExistsUserByUsernameWhenNotExists() {
		repository.save(createUser("bandName","bandPassword"));
		assertFalse(service.existsUserByUsername("band"));
	}
	
	@Test
	public void testExistsUserByUserNameWhenRepositoryIsEmpty() {
		assertFalse(service.existsUserByUsername("anything"));
	}

	private BandDetails createUser(String name, String password) {
		String[] authorities = { "USER" };
		BandDetails result = new BandDetails(name, password, authorities);
		return result;
	}

	private void assertClient(UserDetails c1, UserDetails result) {
		assertEquals(c1.getUsername(), result.getUsername());
		assertEquals(c1.getPassword(), result.getPassword());
	}


}
