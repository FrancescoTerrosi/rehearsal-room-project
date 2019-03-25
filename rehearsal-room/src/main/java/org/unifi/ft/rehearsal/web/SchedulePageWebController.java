package org.unifi.ft.rehearsal.web;

import java.math.BigInteger;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.IllegalFieldValueException;
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
import org.unifi.ft.rehearsal.exceptions.ScheduleNotFoundException;
import org.unifi.ft.rehearsal.model.RehearsalRoom;
import org.unifi.ft.rehearsal.model.Schedule;
import org.unifi.ft.rehearsal.services.Scheduler;

@Controller("HomePageWebController")
@SessionAttributes("user")
public class SchedulePageWebController {

	/*
	 * URIs
	 */
	public static final String CLEAR_SESSION_URI = "/goodbye";
	public static final String SCHEDULE_URI = "/schedule";
	public static final String FIND_BY_NAME_URI = "/schedule/byName";
	public static final String FIND_BY_DATE_URI = "/schedule/byDate";
	public static final String FIND_BY_ROOM_URI = "/schedule/byRoom";
	public static final String DELETE_SCHEDULE_URI = "/schedule/delete";
	public static final String SCHEDULE_PAGE = "schedulePage";
	public static final String REDIRECT = "redirect:";
	public static final String INFO = "info";
	public static final String YOUR_SCHEDULE_DIV = "yourSchedules";
	public static final String QUERIES = "schedules";

	/*
	 * Error Messages
	 */
	public static final String TIME_ERROR_MESSAGE = "Time travel has not been invented yet!";
	public static final String ROOM_ERROR_MESSAGE = "The room you requested for is not free at that time";
	public static final String NUMBER_ERROR_MESSAGE = "Please insert a valid date!";
	public static final String NO_SCHEDULES_MESSAGE = "No schedules found!";
	public static final String SCHEDULE_REMOVED_MESSAGE = "Your schedule was successfully removed";
	public static final String SCHEDULE_SAVED_MESSAGE = "Your schedule was successfully saved!";

	@Autowired
	private Scheduler scheduler;

	@GetMapping(SCHEDULE_URI)
	public String getIndex() {
		return SCHEDULE_PAGE;
	}

	@PostMapping(CLEAR_SESSION_URI)
	public String sayGoodbye(SessionStatus status) {
		status.setComplete();
		return REDIRECT + "/logout";
	}

	@PostMapping(SCHEDULE_URI)
	public ModelAndView rehearsalSchedule(@RequestParam int year, @RequestParam int month, @RequestParam int day,
			@RequestParam int hour, @RequestParam int minutes, @RequestParam RehearsalRoom room,
			@SessionAttribute("user") String band,
			ModelAndView model) {

		DateTime startDate = new DateTime(year, month, day, hour, minutes, 0);
		scheduler.initAndSaveSchedule(band, startDate, room);
		model.addObject(SchedulePageWebController.INFO, SCHEDULE_SAVED_MESSAGE);
		model.setViewName(SCHEDULE_PAGE);
		return model;
	}

	@GetMapping(FIND_BY_NAME_URI)
	public ModelAndView findSchedulesByName(@SessionAttribute("user") String bandName) {
		List<Schedule> schedules = scheduler.findSchedulesByBand(bandName);
		ModelAndView model = addSchedulesToModel(schedules, YOUR_SCHEDULE_DIV);
		model.setViewName(SCHEDULE_PAGE);
		return model;
	}
	
	@GetMapping(FIND_BY_DATE_URI)
	public ModelAndView findSchedulesByDate(
			@RequestParam(value = "year", required = true) int year,
			@RequestParam(value = "month", required = true) int month,
			@RequestParam(value = "day", required = true) int day) {
		List<Schedule> schedules = scheduler.findSchedulesByDate(year, month, day);
		ModelAndView model = addSchedulesToModel(schedules, QUERIES);
		model.setViewName(SCHEDULE_PAGE);
		return model;
	}
	
	@GetMapping(FIND_BY_ROOM_URI)
	public ModelAndView findSchedulesByRoom(@RequestParam(value="room", required = true) RehearsalRoom room) {
		List<Schedule> schedules = scheduler.findSchedulesByRoom(room);
		ModelAndView model = addSchedulesToModel(schedules, QUERIES);
		model.setViewName(SCHEDULE_PAGE);
		return model;
	}
	
	@GetMapping(DELETE_SCHEDULE_URI)
	public ModelAndView deleteSchedule(
			@RequestParam(value = "id", required = true) String id,
			@SessionAttribute("user") String user,
			ModelAndView model) {
		handleError(id, model);
		model.setViewName(SCHEDULE_PAGE);
		return model;
	}

	private void handleError(String id, ModelAndView model) {
		try {
			scheduler.deleteSchedule(new BigInteger(id));
			model.addObject(INFO, SCHEDULE_REMOVED_MESSAGE);
		} catch (ScheduleNotFoundException e) {
			model.addObject(INFO, NO_SCHEDULES_MESSAGE);
			model.setStatus(HttpStatus.BAD_REQUEST);
		}
	}

	private ModelAndView addSchedulesToModel(List<Schedule> schedules, String div) {
		ModelAndView model = new ModelAndView();
		if (schedules.isEmpty()) {
			model.addObject(QUERIES, NO_SCHEDULES_MESSAGE);
		} else {
			model.addObject(div, schedules);
		}
		return model;
	}

	@ExceptionHandler(InvalidTimeException.class)
	private ModelAndView handleInvalidTimeException() {
		ModelAndView result = new ModelAndView();
		result.setViewName(SCHEDULE_PAGE);
		result.addObject(INFO, TIME_ERROR_MESSAGE);
		result.setStatus(HttpStatus.BAD_REQUEST);
		return result;
	}

	@ExceptionHandler(RoomNotFreeException.class)
	private ModelAndView handleRoomNotFreeException() {
		ModelAndView result = new ModelAndView();
		result.setViewName(SCHEDULE_PAGE);
		result.addObject(INFO, ROOM_ERROR_MESSAGE);
		result.setStatus(HttpStatus.BAD_REQUEST);
		return result;
	}

	@ExceptionHandler({ NumberFormatException.class, IllegalFieldValueException.class })
	private ModelAndView handleNumberFormatException() {
		ModelAndView result = new ModelAndView();
		result.setViewName(SCHEDULE_PAGE);
		result.addObject(INFO, NUMBER_ERROR_MESSAGE);
		result.setStatus(HttpStatus.BAD_REQUEST);
		return result;
	}

}
