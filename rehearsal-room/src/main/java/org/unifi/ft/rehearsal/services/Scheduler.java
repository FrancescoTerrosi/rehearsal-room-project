package org.unifi.ft.rehearsal.services;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.unifi.ft.rehearsal.exceptions.InvalidTimeException;
import org.unifi.ft.rehearsal.exceptions.RoomNotFreeException;
import org.unifi.ft.rehearsal.model.Band;
import org.unifi.ft.rehearsal.model.RehearsalRoom;
import org.unifi.ft.rehearsal.model.Schedule;
import org.unifi.ft.rehearsal.mongo.IScheduleMongoRepository;

@Service("Scheduler")
public class Scheduler {

	public static final String ROOM_NOT_FREE = "The requested room is not free at that time!";
	public static final String REQUESTED_DATE_IS_BEFORE_NOW = "The date you request for is not valid! The cause could be: it is in the past or it is 5 minutes ahead this right moment!";
	
	public static final int HOUR_DURATION = 2;
	public static final int MINUTE_DURATION = 30;

	private IScheduleMongoRepository repository;
	
	
	@Autowired
	public Scheduler(IScheduleMongoRepository repository) {
		this.repository = repository;
	}

	public Schedule createSchedule(Band band, DateTime startDate, RehearsalRoom room) {
		if (startDate.isBefore(DateTime.now().plusMinutes(5))) {
			throw new InvalidTimeException(REQUESTED_DATE_IS_BEFORE_NOW);
		}
		DateTime endDate = setEndTime(startDate);
		Schedule result = new Schedule(band, startDate, endDate, room);
		return initAndSaveSchedule(result);
	}

	private Schedule initAndSaveSchedule(Schedule result) {
		if (checkFreeRoom(result)) {
			repository.save(result);
			return result;
		} else {
			throw new RoomNotFreeException(ROOM_NOT_FREE);
		}
	}

	private boolean checkFreeRoom(Schedule s) {
		List<Schedule> schedules = repository.findAll();
		for (int i = 0; i < schedules.size(); i++) {
			Schedule temp = schedules.get(i);
			Interval interval = new Interval(temp.getStartDate(), temp.getEndDate());
			if (temp.getRoom().equals(s.getRoom())
					&& (interval.contains(s.getStartDate()) || interval.contains(s.getEndDate()))) {
				return false;
			}
		}
		return true;
	}

	private DateTime setEndTime(DateTime time) {
		DateTime result = time.plusHours(HOUR_DURATION).plusMinutes(MINUTE_DURATION);
		return result;
	}

}
