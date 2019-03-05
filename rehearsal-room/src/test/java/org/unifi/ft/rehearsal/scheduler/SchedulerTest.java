package org.unifi.ft.rehearsal.scheduler;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Date;

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
		try {
			Schedule result = scheduler.createSchedule(b, start, RehearsalRoom.FIRSTROOM);
			assertNotNull(result);
			assertEquals(start,result.getStartDate());
			assertEquals(end,result.getEndDate());
			assertEquals(b,result.getBand());
			assertEquals(RehearsalRoom.FIRSTROOM, result.getRoom());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
