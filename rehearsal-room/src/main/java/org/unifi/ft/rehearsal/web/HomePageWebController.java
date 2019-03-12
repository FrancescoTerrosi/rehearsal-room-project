package org.unifi.ft.rehearsal.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

@Controller("HomePageWebController")
@SessionAttributes("user")
public class HomePageWebController {

	private static final String HOME_URI = "/home";
	private static final String CLEAR_SESSION = "/clear_session";
	
	private static final String HOME_PAGE = "home";

	@GetMapping(HOME_URI)
	public String getIndex(@SessionAttribute("user") String user) {
		return HOME_PAGE;
	}
	
	@PostMapping(CLEAR_SESSION)
	public String sayGoodbye(SessionStatus status) {
		status.setComplete();
		return "redirect:/";
	}
}
