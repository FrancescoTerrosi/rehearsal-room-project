package org.unifi.ft.rehearsal.web;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.RequestBuilder;

@RunWith(SpringRunner.class)
public class LoginPageWebControllerIT extends AbstractLoginRegisterUtilForIT {

	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	
	@Before
	public void setup() {
		super.setup();
		getRepository().save(createUser("userName", encoder.encode("userPw")));
	}
	
	@Test
	public void testGetLoginIndex() throws Exception {
		getMvc().perform(get("/login"))
			.andExpect(view().name("loginPage")).andExpect(status().isOk());
	}

	@Test
	public void testDoLogin() throws Exception {
		RequestBuilder request = formLogin().user("username", "userName")
				.password("password", "userPw");

		getMvc().perform(request)
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/schedule"));
	}
	
	@Test
	public void testDoWrongLogin() throws Exception {
		RequestBuilder request = formLogin().user("username", "wrongName")
				.password("password", "userPw");

		getMvc().perform(request)
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/login?error"));
	}
	
	@Test
	public void testDoWrongPasswordLogin() throws Exception {
		RequestBuilder request = formLogin().user("username", "userName")
				.password("password", "wrongPw");
		
		getMvc().perform(request)
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/login?error"));
	}
	
}
