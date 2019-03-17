package org.unifi.ft.rehearsal.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.unifi.ft.rehearsal.exceptions.UsernameAlreadyExistsException;
import org.unifi.ft.rehearsal.exceptions.InvalidRegistrationField;
import org.unifi.ft.rehearsal.exceptions.PasswordNotMatchingException;
import org.unifi.ft.rehearsal.services.BandService;

@Controller("RegisterWebController")
public class RegisterPageWebController {

	/*
	 * URIs
	 */
	public static final String REGISTER_URI = "/register";
	public static final String REGISTER_PAGE = "registerPage";
	
	/*
	 * Error messages
	 */
	public static final String ERROR = "error";
	public static final String REGISTRATION_USERNAME_ERROR = "There is already a user with that name";
	public static final String REGISTRATION_PASSW_ERROR = "Passwords do not match, please try again";
	public static final String EMPTY_FIELDS_ERROR = "Please insert non null Username and Password and without spaces!";

	@Autowired
	private BandService bandService;

	@GetMapping(REGISTER_URI)
	public String getRegisterIndex() {
		return REGISTER_PAGE;
	}

	@PostMapping(REGISTER_URI)
	public String performRegister(
			@RequestParam String username, @RequestParam String password,
			@RequestParam String confirmPassword) {
		bandService.register(username, password, confirmPassword);
		return "redirect:/";
	}
	
	@ExceptionHandler(PasswordNotMatchingException.class)
	private ModelAndView handlePasswordNotMatchingException() {
		return addAttributeAndStatus(REGISTRATION_PASSW_ERROR);
	}
	
	@ExceptionHandler(UsernameAlreadyExistsException.class)
	private ModelAndView handleUsernameAlreadyExistsException() {
		return addAttributeAndStatus(REGISTRATION_USERNAME_ERROR);
	}
	
	@ExceptionHandler(InvalidRegistrationField.class)
	private ModelAndView handleEmptyFields() {
		return addAttributeAndStatus(EMPTY_FIELDS_ERROR);
	}
	
	private ModelAndView addAttributeAndStatus(String message) {
		ModelAndView result = new ModelAndView();
		result.setViewName(REGISTER_PAGE);
		result.addObject(ERROR, message);
		result.setStatus(HttpStatus.BAD_REQUEST);
		return result;
	}
	
}
