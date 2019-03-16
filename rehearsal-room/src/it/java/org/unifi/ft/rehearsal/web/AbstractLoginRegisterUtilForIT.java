package org.unifi.ft.rehearsal.web;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import org.junit.After;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.unifi.ft.rehearsal.configurations.MongoConfig;
import org.unifi.ft.rehearsal.configurations.WebSecurityConfig;
import org.unifi.ft.rehearsal.model.BandDetails;
import org.unifi.ft.rehearsal.repository.mongo.IBandDetailsMongoRepository;

@Import({WebSecurityConfig.class, MongoConfig.class})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class AbstractLoginRegisterUtilForIT {

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private IBandDetailsMongoRepository repository;

	private MockMvc mvc;
	
	public void setup() {
		mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
	}
	
	@After
	public void clearAll() {
		repository.deleteAll();
	}
	
	public IBandDetailsMongoRepository getRepository() {
		return this.repository;
	}

	public MockMvc getMvc() {
		return this.mvc;
	}

	protected BandDetails createUser(String name, String password) {
		String[] authorities = { "USER" };
		BandDetails user = new BandDetails(name, password, authorities);
		return user;
	}
}
