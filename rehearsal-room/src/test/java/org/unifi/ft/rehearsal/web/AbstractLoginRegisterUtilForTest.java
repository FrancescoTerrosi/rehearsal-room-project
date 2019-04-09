package org.unifi.ft.rehearsal.web;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import java.math.BigInteger;

import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.unifi.ft.rehearsal.configurations.MongoConfig;
import org.unifi.ft.rehearsal.configurations.WebSecurityConfig;
import org.unifi.ft.rehearsal.model.BandDetails;
import org.unifi.ft.rehearsal.repository.mongo.IBandDetailsMongoRepository;
import org.unifi.ft.rehearsal.services.BandService;

@Import({WebSecurityConfig.class, MongoConfig.class})
public abstract class AbstractLoginRegisterUtilForTest {
	
	private MockMvc mvc;

	@Autowired
	private WebApplicationContext context;

	@MockBean
	private IBandDetailsMongoRepository repository;

	@MockBean
	private BandService service;

	@Before
	public void setup() {
		mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
	}

	@After
	public void clearAll() {
		repository.deleteAll();
	}
	
	protected BandDetails createUser(String name, String password, BigInteger id) {
		String[] authorities = { "USER" };
		BandDetails user = new BandDetails(name, password, authorities);
		user.setId(id);
		return user;
	}
	
	public MockMvc getMvc() {
		return this.mvc;
	}
	
	public IBandDetailsMongoRepository getRepository() {
		return this.repository;
	}
	
	protected BandService getService() {
		return this.service;
	}
	
}
