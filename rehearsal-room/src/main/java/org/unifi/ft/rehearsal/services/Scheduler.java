package org.unifi.ft.rehearsal.services;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.unifi.ft.rehearsal.model.Band;
import org.unifi.ft.rehearsal.model.RehearsalRoom;
import org.unifi.ft.rehearsal.model.Schedule;
import org.unifi.ft.rehearsal.mongo.IScheduleMongoRepository;

@Service("Scheduler")
public class Scheduler {

	private IScheduleMongoRepository repository;
	public static final int HOURDURATION = 2;
	public static final int MINUTEDURATION = 30;

	@Autowired
	public Scheduler(IScheduleMongoRepository repository) {
		this.repository = repository;
	}

	public Schedule createSchedule(Band band, DateTime startDate, RehearsalRoom room) {
		if (startDate.isBefore(DateTime.now().plusMinutes(5))) {
			throw new RuntimeException();
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
			throw new RuntimeException();
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
		DateTime result = time.plusHours(HOURDURATION).plusMinutes(MINUTEDURATION);
		return result;
	}

}
