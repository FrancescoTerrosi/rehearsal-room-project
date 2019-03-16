package org.unifi.ft.rehearsal.web;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller("LoginWebController")
public class LoginPageWebController {

	public static final String INVALID_USERNAME_OR_PASSW = "Wrong username or password!";
	public static final String LOGIN_PAGE = "loginPage";

	@GetMapping("/login")
	public ModelAndView login(@RequestParam(value = "error", required = false) String error) {

		ModelAndView model = new ModelAndView();

		if (error != null) {
			model.addObject("error", INVALID_USERNAME_OR_PASSW);
			model.setStatus(HttpStatus.BAD_REQUEST);
		}
		model.setViewName(LOGIN_PAGE);
		return model;
	}

}
