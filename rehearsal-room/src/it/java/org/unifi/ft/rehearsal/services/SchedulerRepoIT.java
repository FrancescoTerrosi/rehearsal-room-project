package org.unifi.ft.rehearsal.services;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.unifi.ft.rehearsal.exceptions.InvalidTimeException;
import org.unifi.ft.rehearsal.exceptions.RoomNotFreeException;
import org.unifi.ft.rehearsal.exceptions.ScheduleNotFoundException;
import org.unifi.ft.rehearsal.model.BandDetails;
import org.unifi.ft.rehearsal.model.RehearsalRoom;
import org.unifi.ft.rehearsal.model.Schedule;
import org.unifi.ft.rehearsal.repository.mongo.IScheduleMongoRepository;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SchedulerRepoIT {

	private Scheduler service;

	@Autowired
	private IScheduleMongoRepository repository;

	@Before
	public void setup() {
		repository.deleteAll();
		service = new Scheduler(repository);
	}

	@After
	public void cleanRepository() {
		repository.deleteAll();
	}

	@Test
	public void testCreateSchedule() {
		BandDetails b = new BandDetails("bandName", "bandPw");
		DateTime start = new DateTime(2121, 12, 12, 12, 12, 12);
		DateTime end = start.plusHours(Scheduler.HOUR_DURATION).plusMinutes(Scheduler.MINUTE_DURATION);

		service.initAndSaveSchedule(b, start, RehearsalRoom.FIRSTROOM);

		assertEquals(1, repository.count());
		Schedule result = repository.findAll().get(0);

		assertEquals(b, result.getBand());
		assertEquals(start, result.getStartDate());
		assertEquals(end, result.getEndDate());
		assertEquals(RehearsalRoom.FIRSTROOM, result.getRoom());
	}

	@Test
	public void testCreateScheduleSameTimeDifferentRooms() {
		BandDetails b1 = new BandDetails("bandName1", "bandPw1");
		DateTime start = new DateTime(2121, 12, 12, 12, 12, 12);
		DateTime end = start.plusHours(Scheduler.HOUR_DURATION).plusMinutes(Scheduler.MINUTE_DURATION);

		Schedule s = new Schedule(b1, start, end, RehearsalRoom.FIRSTROOM);
		repository.save(s);

		BandDetails b2 = new BandDetails("bandName2", "bandPw2");
		service.initAndSaveSchedule(b2, start, RehearsalRoom.SECONDROOM);

		assertEquals(2, repository.count());
		List<Schedule> list = repository.findAll();

		assertEquals(s, list.get(0));
		Schedule result = list.get(1);
		assertEquals(b2, result.getBand());
		assertEquals(start, result.getStartDate());
		assertEquals(end, result.getEndDate());
		assertEquals(RehearsalRoom.SECONDROOM, result.getRoom());
	}

	@Test
	public void testCreateScheduleSameRoomDifferentTimes() {
		BandDetails b1 = new BandDetails("bandName1", "bandPw1");
		DateTime start1 = new DateTime(2120, 12, 12, 12, 12, 12);
		DateTime end1 = start1.plusHours(Scheduler.HOUR_DURATION).plusMinutes(Scheduler.MINUTE_DURATION);

		Schedule s = new Schedule(b1, start1, end1, RehearsalRoom.FIRSTROOM);
		repository.save(s);

		DateTime start2 = new DateTime(2121, 12, 12, 12, 12, 12);
		DateTime end2 = start2.plusHours(Scheduler.HOUR_DURATION).plusMinutes(Scheduler.MINUTE_DURATION);
		BandDetails b2 = new BandDetails("bandName2", "bandPw2");
		service.initAndSaveSchedule(b2, start2, RehearsalRoom.FIRSTROOM);

		assertEquals(2, repository.count());
		List<Schedule> list = repository.findAll();

		assertEquals(s, list.get(0));
		Schedule result = list.get(1);
		assertEquals(b2, result.getBand());
		assertEquals(start2, result.getStartDate());
		assertEquals(end2, result.getEndDate());
		assertEquals(RehearsalRoom.FIRSTROOM, result.getRoom());
	}

	@Test(expected = InvalidTimeException.class)
	public void testCreateScheduleWhenItIsTooLate() {
		BandDetails b = new BandDetails("bandName", "bandPw");

		service.initAndSaveSchedule(b, new DateTime(new Date()), RehearsalRoom.FIRSTROOM);
	}

	@Test(expected = RoomNotFreeException.class)
	public void testCreateScheduleWhenAnotherExistsOnStartDate() {
		BandDetails b1 = new BandDetails("bandName1", "bandPw1");
		DateTime start = new DateTime(2121, 12, 12, 12, 12, 12);
		DateTime end = start.plusHours(Scheduler.HOUR_DURATION).plusMinutes(Scheduler.MINUTE_DURATION);
		repository.save(new Schedule(b1, start, end, RehearsalRoom.FIRSTROOM));

		BandDetails b2 = new BandDetails("bandName2", "bandPw2");
		service.initAndSaveSchedule(b2, start, RehearsalRoom.FIRSTROOM);
	}

	@Test(expected = RoomNotFreeException.class)
	public void testCreateScheduleWhenAnotherExistsOnEndDate() {
		BandDetails b1 = new BandDetails("bandName1", "bandPw1");
		DateTime start1 = new DateTime(2121, 12, 12, 12, 12, 12);
		DateTime end1 = start1.plusHours(Scheduler.HOUR_DURATION).plusMinutes(Scheduler.MINUTE_DURATION);
		repository.save(new Schedule(b1, start1, end1, RehearsalRoom.FIRSTROOM));

		BandDetails b2 = new BandDetails("bandName2", "bandPw2");
		DateTime start2 = new DateTime(2121, 12, 12, 10, 12, 12);
		service.initAndSaveSchedule(b2, start2, RehearsalRoom.FIRSTROOM);
	}

	@Test
	public void testDeleteSchedule() {
		BandDetails b1 = new BandDetails("bandName1", "bandPw1");
		DateTime start1 = new DateTime(2121, 12, 12, 12, 12, 12);
		DateTime end1 = start1.plusHours(Scheduler.HOUR_DURATION).plusMinutes(Scheduler.MINUTE_DURATION);
		repository.save(new Schedule(b1, start1, end1, RehearsalRoom.FIRSTROOM));

		assertEquals(1, repository.count());

		Schedule result = service.deleteSchedule(b1, start1, RehearsalRoom.FIRSTROOM);

		assertEquals(0, repository.count());
		assertEquals(b1, result.getBand());
		assertEquals(start1, result.getStartDate());
		assertEquals(end1, result.getEndDate());
		assertEquals(RehearsalRoom.FIRSTROOM, result.getRoom());
	}

	@Test(expected = ScheduleNotFoundException.class)
	public void testDeleteScheduleWhenItDoesNotExist() {
		BandDetails b1 = new BandDetails("bandName1", "bandPw1");
		DateTime start1 = new DateTime(2121, 12, 12, 12, 12, 12);
		DateTime end1 = start1.plusHours(Scheduler.HOUR_DURATION).plusMinutes(Scheduler.MINUTE_DURATION);
		repository.save(new Schedule(b1, start1, end1, RehearsalRoom.FIRSTROOM));

		assertEquals(1, repository.count());
		BandDetails b = new BandDetails("bandName", "bandPw");
		DateTime start = new DateTime(new Date());

		service.deleteSchedule(b, start, RehearsalRoom.FIRSTROOM);
	}

	@Test
	public void findSchedulesByBand() {
		BandDetails b1 = new BandDetails("bandName1", "bandPw1");
		BandDetails b2 = new BandDetails("bandName2", "bandPw2");
		DateTime start = new DateTime(2121, 12, 12, 12, 12, 12);
		DateTime end = start.plusHours(Scheduler.HOUR_DURATION).plusMinutes(Scheduler.MINUTE_DURATION);

		Schedule s1 = new Schedule(b1, start, end, RehearsalRoom.FIRSTROOM);
		Schedule s2 = new Schedule(b2, start, end, RehearsalRoom.SECONDROOM);

		repository.save(s1);
		repository.save(s2);
		assertEquals(2, repository.count());

		List<Schedule> result = service.findSchedulesByBand(b1);
		assertEquals(1, result.size());
		assertEquals(s1, result.get(0));
	}

	@Test
	public void findSchedulesByBandWhenThereIsNot() {
		BandDetails b1 = new BandDetails("bandName1", "bandPw1");
		BandDetails b2 = new BandDetails("bandName2", "bandPw2");
		DateTime start = new DateTime(2121, 12, 12, 12, 12, 12);
		DateTime end = start.plusHours(Scheduler.HOUR_DURATION).plusMinutes(Scheduler.MINUTE_DURATION);

		Schedule s1 = new Schedule(b1, start, end, RehearsalRoom.FIRSTROOM);
		Schedule s2 = new Schedule(b2, start, end, RehearsalRoom.SECONDROOM);

		repository.save(s1);
		repository.save(s2);
		assertEquals(2, repository.count());

		List<Schedule> result = service.findSchedulesByBand(new BandDetails("bandName3", "bandPw3"));
		assertEquals(0, result.size());
	}

	@Test
	public void findScheduleByDate() {
		BandDetails b1 = new BandDetails("bandName1", "bandPw1");
		BandDetails b2 = new BandDetails("bandName2", "bandPw2");
		DateTime start = new DateTime(2121, 12, 12, 12, 12, 12);
		DateTime end = start.plusHours(Scheduler.HOUR_DURATION).plusMinutes(Scheduler.MINUTE_DURATION);

		Schedule s1 = new Schedule(b1, start, end, RehearsalRoom.FIRSTROOM);
		Schedule s2 = new Schedule(b2, start, end, RehearsalRoom.SECONDROOM);

		repository.save(s1);
		repository.save(s2);
		assertEquals(2, repository.count());

		List<Schedule> result = service.findSchedulesByDate(2121, 12, 12);
		assertEquals(2, result.size());
		assertEquals(s1, result.get(0));
		assertEquals(s2, result.get(1));
	}

	@Test
	public void findScheduleByDateWrongDay() {

		BandDetails b1 = new BandDetails("bandName1", "bandPw1");
		BandDetails b2 = new BandDetails("bandName2", "bandPw2");
		DateTime start = new DateTime(2121, 12, 12, 12, 12, 12);
		DateTime end = start.plusHours(Scheduler.HOUR_DURATION).plusMinutes(Scheduler.MINUTE_DURATION);

		Schedule s1 = new Schedule(b1, start, end, RehearsalRoom.FIRSTROOM);
		Schedule s2 = new Schedule(b2, start, end, RehearsalRoom.SECONDROOM);

		repository.save(s1);
		repository.save(s2);
		assertEquals(2, repository.count());

		List<Schedule> result = service.findSchedulesByDate(2121, 12, 10);
		assertEquals(0, result.size());
	}

	@Test
	public void findScheduleByDateWrongMonth() {
		BandDetails b1 = new BandDetails("bandName1", "bandPw1");
		BandDetails b2 = new BandDetails("bandName2", "bandPw2");
		DateTime start = new DateTime(2121, 12, 12, 12, 12, 12);
		DateTime end = start.plusHours(Scheduler.HOUR_DURATION).plusMinutes(Scheduler.MINUTE_DURATION);

		Schedule s1 = new Schedule(b1, start, end, RehearsalRoom.FIRSTROOM);
		Schedule s2 = new Schedule(b2, start, end, RehearsalRoom.SECONDROOM);

		repository.save(s1);
		repository.save(s2);
		assertEquals(2, repository.count());

		List<Schedule> result = service.findSchedulesByDate(2121, 10, 12);
		assertEquals(0, result.size());
	}

	@Test
	public void findScheduleByDateWrongYear() {
		BandDetails b1 = new BandDetails("bandName1", "bandPw1");
		BandDetails b2 = new BandDetails("bandName2", "bandPw2");
		DateTime start = new DateTime(2121, 12, 12, 12, 12, 12);
		DateTime end = start.plusHours(Scheduler.HOUR_DURATION).plusMinutes(Scheduler.MINUTE_DURATION);

		Schedule s1 = new Schedule(b1, start, end, RehearsalRoom.FIRSTROOM);
		Schedule s2 = new Schedule(b2, start, end, RehearsalRoom.SECONDROOM);

		repository.save(s1);
		repository.save(s2);
		assertEquals(2, repository.count());

		List<Schedule> result = service.findSchedulesByDate(2120, 12, 12);
		assertEquals(0, result.size());
	}

	@Test
	public void findScheduleByRoom() {
		BandDetails b1 = new BandDetails("bandName1", "bandPw1");
		BandDetails b2 = new BandDetails("bandName2", "bandPw2");
		DateTime start = new DateTime(2121, 12, 12, 12, 12, 12);
		DateTime end = start.plusHours(Scheduler.HOUR_DURATION).plusMinutes(Scheduler.MINUTE_DURATION);

		Schedule s1 = new Schedule(b1, start, end, RehearsalRoom.FIRSTROOM);
		Schedule s2 = new Schedule(b2, start, end, RehearsalRoom.SECONDROOM);

		repository.save(s1);
		repository.save(s2);
		assertEquals(2, repository.count());

		List<Schedule> result = service.findSchedulesByRoom(RehearsalRoom.FIRSTROOM);
		assertEquals(1, result.size());
		assertEquals(s1, result.get(0));
	}

	@Test
	public void findScheduleByRoomWhenItIsTooLate() {
		BandDetails b1 = new BandDetails("bandName1", "bandPw1");
		BandDetails b2 = new BandDetails("bandName2", "bandPw2");
		DateTime start = new DateTime(2018, 12, 12, 12, 12, 12);
		DateTime end = start.plusHours(Scheduler.HOUR_DURATION).plusMinutes(Scheduler.MINUTE_DURATION);

		Schedule s1 = new Schedule(b1, start, end, RehearsalRoom.FIRSTROOM);
		Schedule s2 = new Schedule(b2, start, end, RehearsalRoom.SECONDROOM);

		repository.save(s1);
		repository.save(s2);
		assertEquals(2, repository.count());

		List<Schedule> result1 = service.findSchedulesByRoom(RehearsalRoom.FIRSTROOM);
		assertEquals(0, result1.size());

		List<Schedule> result2 = service.findSchedulesByRoom(RehearsalRoom.SECONDROOM);
		assertEquals(0, result2.size());
	}

	@Test
	public void findScheduleByRoomWhenRoomIsEmpty() {
		BandDetails b1 = new BandDetails("bandName1", "bandPw1");
		BandDetails b2 = new BandDetails("bandName2", "bandPw2");
		DateTime start = new DateTime(2018, 12, 12, 12, 12, 12);
		DateTime end = start.plusHours(Scheduler.HOUR_DURATION).plusMinutes(Scheduler.MINUTE_DURATION);

		Schedule s1 = new Schedule(b1, start, end, RehearsalRoom.FIRSTROOM);
		Schedule s2 = new Schedule(b2, start, end, RehearsalRoom.SECONDROOM);

		repository.save(s1);
		repository.save(s2);
		assertEquals(2, repository.count());

		List<Schedule> result = service.findSchedulesByRoom(RehearsalRoom.THIRDROOM);
		assertEquals(0, result.size());
	}
}
