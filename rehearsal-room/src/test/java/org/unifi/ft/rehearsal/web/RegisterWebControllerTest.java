package org.unifi.ft.rehearsal.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.mockito.BDDMockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.MultiValueMap;
import org.unifi.ft.rehearsal.exceptions.UsernameAlreadyExistsException;
import org.unifi.ft.rehearsal.exceptions.PasswordNotMatchingException;
import org.unifi.ft.rehearsal.web.RegisterWebController;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = RegisterWebController.class)
public class RegisterWebControllerTest extends AbstractLoginRegisterUtilForTest {
	
	private MultiValueMap<String, String> params = new HttpHeaders();
	
	@Before
	public void setup() {
		super.setup();
		params.clear();
	}
	
	@After
	public void clearAll() {
		super.clearAll();
		params.clear();
	}

	@Test
	public void testGetRegisterIndex() throws Exception {
		getMvc().perform(get(RegisterWebController.REGISTER_URI))
			.andExpect(view().name(RegisterWebController.REGISTER_PAGE))
			.andExpect(status().isOk());
	}

	@Test
	public void testDoRegister() throws Exception {
		UserDetails user = createUser("userName", "userPassword");
		params.add("username", "userName");
		params.add("password", "userPassword");
		params.add("confirmPassword", "userPassword");

		given(getService().register("userName", "userPassword","userPassword")).willReturn(user);

		getMvc().perform(post("/register").params(params))
			.andExpect(view().name("redirect:/"))
			.andExpect(status().is3xxRedirection());
		
		verify(getService()).register("userName", "userPassword", "userPassword");
	}

	@Test
	public void testDoInvalidUsernameRegister() throws Exception {
		params.add("username", "userName");
		params.add("password", "userPassword");
		params.add("confirmPassword", "userPassword");

		given(getService().register("userName", "userPassword", "userPassword"))
			.willThrow(UsernameAlreadyExistsException.class);

		getMvc().perform(post("/register").params(params))
			.andExpect(view().name("redirect:"+RegisterWebController.REGISTER_URI+RegisterWebController.INVALID_USER_URI));
		
		verify(getService()).register("userName", "userPassword", "userPassword");
	}
	
	@Test
	public void testInvalidUsernameParam() throws Exception {
		params.add("invalidUsername", "true");
		getMvc().perform(get(RegisterWebController.REGISTER_URI+RegisterWebController.INVALID_USER_URI).params(params))
			.andExpect(view().name(RegisterWebController.REGISTER_PAGE))
			.andExpect(model().attribute("error", RegisterWebController.REGISTRATION_USERNAME_ERROR))
			.andExpect(status().is4xxClientError());
	}
	
	@Test
	public void testDoInvalidRegisterPasswordsNotMatching() throws Exception {
		params.add("username", "userName");
		params.add("password", "userPassword");
		params.add("confirmPassword", "errorPassword");
		
		given(getService().register("userName", "userPassword", "errorPassword"))
			.willThrow(PasswordNotMatchingException.class);

		getMvc().perform(post("/register").params(params))
			.andExpect(view().name("redirect:"+RegisterWebController.REGISTER_URI+RegisterWebController.INVALID_PASSW_URI));
		
		verify(getService()).register("userName", "userPassword", "errorPassword");
	}
	
	@Test
	public void testInvalidPasswordsParam() throws Exception {
		params.add("invalidPasswords", "true");
		getMvc().perform(get(RegisterWebController.REGISTER_URI+RegisterWebController.INVALID_PASSW_URI).params(params))
			.andExpect(view().name(RegisterWebController.REGISTER_PAGE))
			.andExpect(model().attribute("error", RegisterWebController.REGISTRATION_PASSW_ERROR))
			.andExpect(status().is4xxClientError());
	}

}
