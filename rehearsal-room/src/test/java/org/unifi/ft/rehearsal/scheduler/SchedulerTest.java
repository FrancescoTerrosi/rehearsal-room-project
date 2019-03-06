package org.unifi.ft.rehearsal.scheduler;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.unifi.ft.rehearsal.model.Band;
import org.unifi.ft.rehearsal.model.RehearsalRoom;
import org.unifi.ft.rehearsal.model.Schedule;
import org.unifi.ft.rehearsal.mongo.IScheduleMongoRepository;
import org.unifi.ft.rehearsal.services.Scheduler;

public class SchedulerTest {

	private Scheduler scheduler;
	private IScheduleMongoRepository repository;

	@Before
	public void init() {
		repository = mock(IScheduleMongoRepository.class);
		scheduler = new Scheduler(repository);
	}

	@Test
	public void testCreateSchedule() {
		when(repository.findAll()).thenReturn(new ArrayList<Schedule>());

		DateTime start = new DateTime(new Date());
		DateTime end = start.plusHours(Scheduler.HOURDURATION).plusMinutes(Scheduler.MINUTEDURATION);
		Band b = new Band("ProvaBand", "ProvaPw");

		Schedule result = scheduler.createSchedule(b, start, RehearsalRoom.FIRSTROOM);
		assertNotNull(result);
		assertEquals(start, result.getStartDate());
		assertEquals(end, result.getEndDate());
		assertEquals(b, result.getBand());
		assertEquals(RehearsalRoom.FIRSTROOM, result.getRoom());
		verify(repository,times(1)).findAll();
	}
	
	@Test
	public void testCreateScheduleSameTimeDifferentRooms() {
		when(repository.findAll()).thenReturn(notEmptyList());

		DateTime start = new DateTime(2121, 12, 12, 12, 12, 12);
		DateTime end = start.plusHours(Scheduler.HOURDURATION).plusMinutes(Scheduler.MINUTEDURATION);
		Band b = new Band("ProvaBand", "ProvaPw");

		Schedule result = scheduler.createSchedule(b, start, RehearsalRoom.SECONDROOM);
		assertNotNull(result);
		assertEquals(start, result.getStartDate());
		assertEquals(end, result.getEndDate());
		assertEquals(b, result.getBand());
		assertEquals(RehearsalRoom.SECONDROOM, result.getRoom());
		verify(repository,times(1)).findAll();
	}
	
	@Test
	public void testCreateScehduleSameRoomDifferentTimes() {
		when(repository.findAll()).thenReturn(notEmptyList());

		DateTime start = new DateTime(2121, 12, 12, 16, 12, 12);
		DateTime end = start.plusHours(Scheduler.HOURDURATION).plusMinutes(Scheduler.MINUTEDURATION);
		Band b = new Band("ProvaBand", "ProvaPw");

		Schedule result = scheduler.createSchedule(b, start, RehearsalRoom.FIRSTROOM);
		assertNotNull(result);
		assertEquals(start, result.getStartDate());
		assertEquals(end, result.getEndDate());
		assertEquals(b, result.getBand());
		assertEquals(RehearsalRoom.FIRSTROOM, result.getRoom());
		verify(repository,times(1)).findAll();
	}

	@Test(expected = RuntimeException.class)
	public void testCreateScheduleWhenAnotherExistsOnStartDate() {
		when(repository.findAll()).thenReturn(notEmptyList());

		DateTime start = new DateTime(2121, 12, 12, 12, 12, 12);
		Band b = new Band("ProvaBand", "ProvaPw");

		Schedule result = scheduler.createSchedule(b, start, RehearsalRoom.FIRSTROOM);
		assertNull(result);
	}
	
	@Test(expected = RuntimeException.class)
	public void testCreateScheduleWhenAnotherExistsOnEndDate() {
		when(repository.findAll()).thenReturn(notEmptyList());

		DateTime start = new DateTime(2121, 12, 12, 10, 12, 12);
		Band b = new Band("ProvaBand", "ProvaPw");

		Schedule result = scheduler.createSchedule(b, start, RehearsalRoom.FIRSTROOM);
		assertNull(result);
	}

	private List<Schedule> notEmptyList() {
		List<Schedule> result = new ArrayList<>();
		Band b = new Band("name", "pw");
		DateTime start = new DateTime(2121, 12, 12, 12, 12, 12);
		DateTime end = start.plusHours(Scheduler.HOURDURATION).plusMinutes(Scheduler.MINUTEDURATION);
		Schedule s = new Schedule(b, start, end, RehearsalRoom.FIRSTROOM);
		result.add(s);
		return result;
	}

}
