package org.unifi.ft.rehearsal.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.unifi.ft.rehearsal.exceptions.UsernameAlreadyExistsException;
import org.unifi.ft.rehearsal.exceptions.PasswordNotMatchingException;
import org.unifi.ft.rehearsal.services.BandService;

@Controller("RegisterWebController")
public class RegisterWebController {

	/*
	 * URIs
	 */
	public static final String REGISTER_URI = "/register";
	public static final String REGISTER_PAGE = "registerPage";
	public static final String INVALID_USER_URI = "?invalidUsername";
	public static final String INVALID_PASSW_URI = "?invalidPasswords";
	
	/*
	 * Error messages
	 */
	public static final String REGISTRATION_USERNAME_ERROR = "There is already a user with that name";
	public static final String REGISTRATION_PASSW_ERROR = "Passwords do not match, please try again";


	@Autowired
	private BandService bandService;

	@GetMapping(REGISTER_URI)
	public ModelAndView getRegisterIndex(
			@RequestParam(value="invalidUsername", required = false) String invalidUsername, 
			@RequestParam(value="invalidPasswords", required = false) String invalidPasswords ) {
		ModelAndView result = new ModelAndView();
		if (invalidUsername != null) {
			result.addObject("error", REGISTRATION_USERNAME_ERROR);
			result.setStatus(HttpStatus.BAD_REQUEST);
		} else if (invalidPasswords != null) {
			result.addObject("error", REGISTRATION_PASSW_ERROR);
			result.setStatus(HttpStatus.BAD_REQUEST);
		}
		result.setViewName(REGISTER_PAGE);
		return result;
	}

	@PostMapping(REGISTER_URI)
	public String performRegister(@RequestParam String username, @RequestParam String password,
			@RequestParam String confirmPassword, Model model) {
		try {
			bandService.register(username, password, confirmPassword);
			return "redirect:/";
		} catch (UsernameAlreadyExistsException e) {
			return "redirect:"+REGISTER_URI+INVALID_USER_URI;
		} catch (PasswordNotMatchingException e) {
			return "redirect:"+REGISTER_URI+INVALID_PASSW_URI;
		}
	}
	
}
