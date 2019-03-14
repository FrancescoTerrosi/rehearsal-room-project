package org.unifi.ft.rehearsal.web;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.unifi.ft.rehearsal.exceptions.InvalidTimeException;
import org.unifi.ft.rehearsal.exceptions.RoomNotFreeException;
import org.unifi.ft.rehearsal.model.RehearsalRoom;
import org.unifi.ft.rehearsal.services.Scheduler;

@Controller("HomePageWebController")
@SessionAttributes("user")
public class HomePageWebController {

	/*
	 * URIs
	 */
	public static final String HOME_URI = "/home";
	public static final String CLEAR_SESSION_URI = "/clear_session";
	public static final String SCHEDULE_URI = "/schedule";
	public static final String ROOM_ERROR_URI = "?roomError";
	public static final String TIME_ERROR_URI = "?timeError";
	public static final String NUMBER_FORMAT_ERROR_URI = "?numberError";
	public static final String HOME_PAGE_URI = "home";
	public static final String REDIRECT = "redirect:";
	public static final String ERROR = "error";

	/*
	 * Error Messages
	 */
	public static final String TIME_ERROR_MESSAGE = "Time travel has not been invented yet!";
	public static final String ROOM_ERROR_MESSAGE = "The room you requested for is not free at that time";
	public static final String NUMBER_ERROR_MESSAGE = "Please insert a valid date!";

	@Autowired
	private Scheduler scheduler;

	@GetMapping(HOME_URI)
	public ModelAndView getIndex(
			@RequestParam(value = "numberError", required = false) String numberError,
			@RequestParam(value = "roomError", required = false) String roomError,
			@RequestParam(value = "timeError", required = false) String timeError) {
		ModelAndView model = handleError(numberError, roomError, timeError);
		model.setViewName(HOME_PAGE_URI);
		return model;
	}

	@PostMapping(CLEAR_SESSION_URI)
	public String sayGoodbye(SessionStatus status) {
		status.setComplete();
		return REDIRECT+"/";
	}

	@PostMapping(SCHEDULE_URI)
	public String rehearsalSchedule(
			@RequestParam int year, @RequestParam int month, @RequestParam int day,
			@RequestParam int hour, @RequestParam int minutes, @RequestParam RehearsalRoom room,
			@SessionAttribute("user") String band) {

		DateTime startDate = new DateTime(year, month, day, hour, minutes, 0);
		try {
			scheduler.initAndSaveSchedule(band, startDate, room);
		} catch (InvalidTimeException e) {
			return REDIRECT + HOME_URI + TIME_ERROR_URI;
		} catch (RoomNotFreeException e) {
			return REDIRECT + HOME_URI + ROOM_ERROR_URI;
		}
		return HOME_PAGE_URI;
	}
	
	@ExceptionHandler(NumberFormatException.class)
	private String handleError() {
		return REDIRECT + HOME_URI + NUMBER_FORMAT_ERROR_URI;
	}
	
	private ModelAndView handleError(String numberError, String roomError, String timeError) {
		ModelAndView model = new ModelAndView();
		if (numberError != null) {
			model.addObject(ERROR, NUMBER_ERROR_MESSAGE);
			model.setStatus(HttpStatus.BAD_REQUEST);
		} else if (roomError != null) {
			model.addObject(ERROR, ROOM_ERROR_MESSAGE);
			model.setStatus(HttpStatus.BAD_REQUEST);
		} else if (timeError != null) {
			model.addObject(ERROR, TIME_ERROR_MESSAGE);
			model.setStatus(HttpStatus.BAD_REQUEST);
		}
		return model;
	}

}
