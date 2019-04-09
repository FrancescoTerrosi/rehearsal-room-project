package org.unifi.ft.rehearsal.web;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigInteger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.RequestBuilder;
import org.unifi.ft.rehearsal.model.BandDetails;
import org.unifi.ft.rehearsal.web.LoginPageWebController;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = LoginPageWebController.class)
public class LoginPageWebControllerTest extends AbstractLoginRegisterUtilForTest {

	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	
	@Test
	public void testGetLoginIndex() throws Exception {
		getMvc().perform(get("/login"))
			.andExpect(view().name("loginPage")).andExpect(status().isOk());
	}

	@Test
	public void testDoLogin() throws Exception {
		BandDetails user = createUser("userName", encoder.encode("userPassword"), new BigInteger("1"));
		given(getService().loadUserByUsername("userName")).willReturn(user);

		RequestBuilder request = formLogin().user("username", "userName")
				.password("password", "userPassword");

		getMvc().perform(request)
			.andExpect(status().is3xxRedirection());
		
		verify(getService()).loadUserByUsername("userName");
	}

	@Test
	public void testDoWrongNameLogin() throws Exception {
		given(getService().loadUserByUsername("useryName")).willThrow(UsernameNotFoundException.class);

		RequestBuilder request = formLogin().user("username", "useryName")
				.password("password", "userPassword");

		getMvc().perform(request)
				.andExpect(redirectedUrl("/login?error"))
				.andExpect(status().is3xxRedirection());

		verify(getService()).loadUserByUsername("useryName");
	}
	
	@Test
	public void testWrongNameModelAttribute() throws Exception {
		getMvc().perform(get("/login?error").param("error", "true"))
			.andExpect(view().name(LoginPageWebController.LOGIN_PAGE))
					.andExpect(model().attribute("error", LoginPageWebController.INVALID_USERNAME_OR_PASSW))
					.andExpect(status().is4xxClientError());
	}
	
	@Test
	public void testDoWrongPasswordLogin() throws Exception {
		given(getService().loadUserByUsername("userName")).willReturn(createUser("username", "userPassword", new BigInteger("1")));
		
		RequestBuilder request = formLogin().user("username", "userName")
				.password("password", "useryPassword");
		
		getMvc().perform(request)
			.andExpect(redirectedUrl("/login?error"))
			.andExpect(status().is3xxRedirection());
		
		verify(getService()).loadUserByUsername("userName");
	}
	
	@Test
	public void testWrongPasswordModelAttribute() throws Exception {
		getMvc().perform(get("/login?error").param("error", "true"))
			.andExpect(view().name(LoginPageWebController.LOGIN_PAGE))
					.andExpect(model().attribute("error", LoginPageWebController.INVALID_USERNAME_OR_PASSW))
					.andExpect(status().is4xxClientError());
	}
	
}
