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
import org.unifi.ft.rehearsal.exceptions.InvalidRegistrationField;
import org.unifi.ft.rehearsal.exceptions.PasswordNotMatchingException;
import org.unifi.ft.rehearsal.web.RegisterPageWebController;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = RegisterPageWebController.class)
public class RegisterPageWebControllerTest extends AbstractLoginRegisterUtilForTest {

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
		getMvc().perform(get(RegisterPageWebController.REGISTER_URI))
				.andExpect(view().name(RegisterPageWebController.REGISTER_PAGE)).andExpect(status().isOk());
	}

	@Test
	public void testDoRegister() throws Exception {
		UserDetails user = createUser("userName", "userPassword");
		params.add("username", "userName");
		params.add("password", "userPassword");
		params.add("confirmPassword", "userPassword");

		given(getService().register("userName", "userPassword", "userPassword")).willReturn(user);

		getMvc().perform(post("/register").params(params))
				.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/"));

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
				.andExpect(status().is4xxClientError())
				.andExpect(model().attribute("error", RegisterPageWebController.REGISTRATION_USERNAME_ERROR));

		verify(getService()).register("userName", "userPassword", "userPassword");
	}

	@Test
	public void testDoInvalidRegisterPasswordsNotMatching() throws Exception {
		params.add("username", "userName");
		params.add("password", "userPassword");
		params.add("confirmPassword", "errorPassword");

		given(getService().register("userName", "userPassword", "errorPassword"))
				.willThrow(PasswordNotMatchingException.class);

		getMvc().perform(post("/register").params(params))
				.andExpect(status().is4xxClientError())
				.andExpect(model().attribute("error", RegisterPageWebController.REGISTRATION_PASSW_ERROR));

		verify(getService()).register("userName", "userPassword", "errorPassword");
	}

	@Test
	public void testDoInvalidRegisterEmptyUsername() throws Exception {
		params.add("username", "");
		params.add("password", "userPassword");
		params.add("confirmPassword", "confirmPassword");

		given(getService().register("", "userPassword", "confirmPassword"))
				.willThrow(InvalidRegistrationField.class);

		getMvc().perform(post("/register").params(params))
				.andExpect(status().is4xxClientError())
				.andExpect(model().attribute("error", RegisterPageWebController.EMPTY_FIELDS_ERROR));

		verify(getService()).register("", "userPassword", "confirmPassword");
	}
	
	@Test
	public void testDoInvalidRegisterEmptyPw() throws Exception {
		params.add("username", "userName");
		params.add("password", "");
		params.add("confirmPassword", "confirmPassword");

		given(getService().register("userName", "", "confirmPassword"))
				.willThrow(InvalidRegistrationField.class);

		getMvc().perform(post("/register").params(params))
				.andExpect(status().is4xxClientError())
				.andExpect(model().attribute("error", RegisterPageWebController.EMPTY_FIELDS_ERROR));

		verify(getService()).register("userName", "", "confirmPassword");
	}
	
	@Test
	public void testDoInvalidRegisterEmptyConfirmPw() throws Exception {
		params.add("username", "userName");
		params.add("password", "userPassword");
		params.add("confirmPassword", "");

		given(getService().register("userName", "userPassword", ""))
				.willThrow(InvalidRegistrationField.class);

		getMvc().perform(post("/register").params(params))
				.andExpect(status().is4xxClientError())
				.andExpect(model().attribute("error", RegisterPageWebController.EMPTY_FIELDS_ERROR));

		verify(getService()).register("userName", "userPassword", "");
	}
	@Test
	public void testDoInvalidRegisterUsernameWithSpaces() throws Exception {
		params.add("username", "u s e r n a m e");
		params.add("password", "userPassword");
		params.add("confirmPassword", "confirmPassword");

		given(getService().register("u s e r n a m e", "userPassword", "confirmPassword"))
				.willThrow(InvalidRegistrationField.class);

		getMvc().perform(post("/register").params(params))
				.andExpect(status().is4xxClientError())
				.andExpect(model().attribute("error", RegisterPageWebController.EMPTY_FIELDS_ERROR));

		verify(getService()).register("u s e r n a m e", "userPassword", "confirmPassword");
	}
	
	@Test
	public void testDoInvalidRegisterPwWithSpaces() throws Exception {
		params.add("username", "userName");
		params.add("password", " ");
		params.add("confirmPassword", " ");

		given(getService().register("userName", " ", " "))
				.willThrow(InvalidRegistrationField.class);

		getMvc().perform(post("/register").params(params))
				.andExpect(status().is4xxClientError())
				.andExpect(model().attribute("error", RegisterPageWebController.EMPTY_FIELDS_ERROR));

		verify(getService()).register("userName", " ", " ");
	}
	
}
