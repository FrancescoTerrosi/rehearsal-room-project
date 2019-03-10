package org.unifi.ft.rehearsal.services;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.unifi.ft.rehearsal.services.BandService;
import org.unifi.ft.rehearsal.exceptions.PasswordNotMatchingException;
import org.unifi.ft.rehearsal.exceptions.UsernameAlreadyExistsException;
import org.unifi.ft.rehearsal.model.BandDetails;
import org.unifi.ft.rehearsal.repository.mongo.IBandDetailsMongoRepository;

public class BandServiceTest {

	private IBandDetailsMongoRepository repository;
	private PasswordEncoder encoder;
	private BandService service;

	@Before
	public void init() {
		encoder = mock(PasswordEncoder.class);
		repository = mock(IBandDetailsMongoRepository.class);
		service = new BandService(repository,encoder);
	}

	@Test
	public void testValidRegistration() {
		when(repository.findAll()).thenReturn(notEmptyList());
		UserDetails c = createUser("bandName", "bandPassword");
		when(repository.save(isA(UserDetails.class))).thenReturn(c);
		when(encoder.encode(anyString())).thenReturn("bandPassword");
		
		UserDetails result = service.register("bandName","bandPassword","bandPassword");
		assertEquals("bandName", result.getUsername());
		assertEquals("bandPassword", result.getPassword());
		verify(repository, times(1)).save(isA(UserDetails.class));
	}

	@Test(expected = UsernameAlreadyExistsException.class)
	public void testInvalidRegistrationUsernameAlreadyExists() {
		UserDetails c = createUser("band", "pass1");
		when(repository.findAll()).thenReturn(notEmptyList());
		when(repository.save(isA(UserDetails.class))).thenReturn(c);
		
		service.register("band1","pass1","pass1");
		verify(repository, times(1)).save(isA(UserDetails.class));
	}
	
	@Test(expected = PasswordNotMatchingException.class)
	public void testInvalidRegistrationPasswordNotMatching() {
		service.register("band", "bandPassword", "errorPassword");
	}
	
	@Test
	public void testLoadUserByUsername() {
		when(repository.findAll()).thenReturn(notEmptyList());
		
		UserDetails result = service.loadUserByUsername("band1");
		assertThat(result,instanceOf(UserDetails.class));
		assertEquals("band1",result.getUsername());
		assertEquals("pass1",result.getPassword());
	}
	
	@Test(expected=UsernameNotFoundException.class)
	public void testLoadUserByUsernameWhenUserDoesNotExist() {
		when(repository.findAll()).thenReturn(notEmptyList());
		
		service.loadUserByUsername("errorBand");
		verify(repository,times(1)).findAll();
	}
	
	@Test
	public void existsUserByUsername() {
		when(repository.findAll()).thenReturn(notEmptyList());

		assertTrue(service.existsUserByUsername("band1"));
		assertTrue(service.existsUserByUsername("band2"));
		assertTrue(service.existsUserByUsername("band3"));
	}
	
	@Test
	public void existsUserByUsernameWhenNotExists() {
		when(repository.findAll()).thenReturn(notEmptyList());
		
		assertFalse(service.existsUserByUsername("band4"));
	}
	
	
	private List<UserDetails> notEmptyList() {
		List<UserDetails> result = new LinkedList<>();
		result.add(createUser("band1","pass1"));
		result.add(createUser("band2","pass2"));
		result.add(createUser("band3","pass3"));
		return result;
}

	private UserDetails createUser(String name, String password) {
		String[] authorities = {"USER"};
		UserDetails result = new BandDetails(name,password,authorities);
		return result;
	}

}
