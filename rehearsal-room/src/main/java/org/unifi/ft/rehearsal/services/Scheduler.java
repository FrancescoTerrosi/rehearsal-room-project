package org.unifi.ft.rehearsal.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.unifi.ft.rehearsal.exceptions.InvalidTimeException;
import org.unifi.ft.rehearsal.exceptions.RoomNotFreeException;
import org.unifi.ft.rehearsal.exceptions.ScheduleNotFoundException;
import org.unifi.ft.rehearsal.model.BandDetails;
import org.unifi.ft.rehearsal.model.RehearsalRoom;
import org.unifi.ft.rehearsal.model.Schedule;
import org.unifi.ft.rehearsal.repository.mongo.IScheduleMongoRepository;

@Service("Scheduler")
public class Scheduler {

	public static final String ROOM_NOT_FREE = "The requested room is not free at that time!";
	public static final String REQUESTED_DATE_IS_BEFORE_NOW = "The date you request for is not valid! The cause could be: it is in the past or it is 5 minutes ahead this right moment!";
	public static final String SCHEDULE_NOT_FOUND = "Schedule not found!";

	public static final int HOUR_DURATION = 2;
	public static final int MINUTE_DURATION = 30;

	private static final Logger LOGGER = LogManager.getLogger(Scheduler.class);

	private IScheduleMongoRepository repository;

	@Autowired
	public Scheduler(IScheduleMongoRepository repository) {
		this.repository = repository;
	}

	public List<Schedule> findSchedulesByBand(BandDetails band) {
		List<Schedule> schedules = repository.findAll();
		List<Schedule> result = new ArrayList<>();
		for (Schedule schedule : schedules) {
			if (schedule.getBand().equals(band)) {
				result.add(schedule);
			}
		}
		return result;
	}

	public List<Schedule> findSchedulesByDate(int year, int month, int day) {
		List<Schedule> schedules = repository.findAll();
		List<Schedule> result = new ArrayList<>();
		for (Schedule schedule : schedules) {
			DateTime date = schedule.getStartDate();
			if (date.getYear() == year && date.getMonthOfYear() == month && date.getDayOfMonth() == day) {
				result.add(schedule);
			}
		}
		return result;
	}

	public List<Schedule> findSchedulesByRoom(RehearsalRoom room) {
		List<Schedule> schedules = repository.findAll();
		List<Schedule> result = new ArrayList<>();
		for (Schedule schedule : schedules) {
			if (schedule.getEndDate().isBeforeNow()) {
				continue;
			}
			if (schedule.getRoom().equals(room)) {
				result.add(schedule);
			}
		}
		return result;
	}

	public Schedule deleteSchedule(BandDetails band, DateTime startDate, RehearsalRoom room) {
		List<Schedule> schedules = repository.findAll();
		Schedule toDelete = createSchedule(band, startDate, room);
		for (Schedule schedule : schedules) {
			if (schedule.equals(toDelete)) {
				repository.delete(schedule);
				return schedule;
			}
		}
		LOGGER.warn(band.getUsername() + " - " + SCHEDULE_NOT_FOUND);
		throw new ScheduleNotFoundException(SCHEDULE_NOT_FOUND);
	}

	public Schedule initAndSaveSchedule(BandDetails band, DateTime startDate, RehearsalRoom room) {
		if (startDate.isBefore(DateTime.now().plusMinutes(5))) {
			LOGGER.warn(band.getUsername() + " - " + REQUESTED_DATE_IS_BEFORE_NOW);
			throw new InvalidTimeException(REQUESTED_DATE_IS_BEFORE_NOW);
		}
		Schedule result = createSchedule(band, startDate, room);
		if (checkFreeRoom(result)) {
			repository.save(result);
			return result;
		} else {
			LOGGER.info(band.getUsername() + " - " + ROOM_NOT_FREE);
			throw new RoomNotFreeException(ROOM_NOT_FREE);
		}
	}

	private Schedule createSchedule(BandDetails band, DateTime startDate, RehearsalRoom room) {
		DateTime endDate = setEndTime(startDate);
		return new Schedule(band, startDate, endDate, room);
	}

	private boolean checkFreeRoom(Schedule s) {
		List<Schedule> schedules = repository.findAll();
		for (Schedule schedule : schedules) {
			Interval interval = new Interval(schedule.getStartDate(), schedule.getEndDate());
			if (schedule.getRoom().equals(s.getRoom())
					&& (interval.contains(s.getStartDate()) || interval.contains(s.getEndDate()))) {
				return false;
			}
		}
		return true;
	}

	private DateTime setEndTime(DateTime time) {
		return time.plusHours(HOUR_DURATION).plusMinutes(MINUTE_DURATION);
	}

}
