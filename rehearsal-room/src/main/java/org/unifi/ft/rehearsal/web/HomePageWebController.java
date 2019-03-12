package org.unifi.ft.rehearsal.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller("HomePageWebController")
@SessionAttributes("user")
public class HomePageWebController {

	private static final String HOME_URI = "/home";
	
	private static final String HOME_PAGE = "home";

	@GetMapping(HOME_URI)
	public String getIndex(@SessionAttribute("user") String user) {
		return HOME_PAGE;
	}
}
