package org.unifi.ft.rehearsal.web;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.unifi.ft.rehearsal.exceptions.InvalidTimeException;
import org.unifi.ft.rehearsal.exceptions.RoomNotFreeException;
import org.unifi.ft.rehearsal.model.RehearsalRoom;
import org.unifi.ft.rehearsal.services.Scheduler;

@Controller("HomePageWebController")
@SessionAttributes("user")
public class HomePageWebController {

	private static final String HOME_URI = "/home";
	private static final String CLEAR_SESSION = "/clear_session";
	private static final String SCHEDULE = "/schedule";

	private static final String HOME_PAGE = "home";

	@Autowired
	private Scheduler scheduler;

	@GetMapping(HOME_URI)
	public String getIndex(@SessionAttribute("user") String user) {
		return HOME_PAGE;
	}

	@PostMapping(CLEAR_SESSION)
	public String sayGoodbye(SessionStatus status) {
		status.setComplete();
		return "redirect:/";
	}

	@PostMapping(SCHEDULE)
	public String rehearsalSchedule(
			@RequestParam int year, @RequestParam int month, @RequestParam int day,
			@RequestParam int hour, @RequestParam int minutes, @RequestParam RehearsalRoom room,
			@SessionAttribute("user") String band,
			@RequestParam(value = "timeError", required = false) String timeError,
			@RequestParam(value = "roomError", required = false) String roomError) {

		DateTime startDate = new DateTime(year, month, day, hour, minutes, 0);
		try {
			scheduler.initAndSaveSchedule(band, startDate, room);
		} catch (InvalidTimeException e) {
			/*
			 * TODO: SHOW ERROR IN PAGE
			 */
		} catch (RoomNotFreeException e) {
			/*
			 * TODO: SHOW ERROR IN PAGE
			 */
		}
		return HOME_PAGE;
	}
}
