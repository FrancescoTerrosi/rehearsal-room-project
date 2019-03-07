package org.unifi.ft.rehearsal.scheduler;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.unifi.ft.rehearsal.exceptions.InvalidTimeException;
import org.unifi.ft.rehearsal.exceptions.RoomNotFreeException;
import org.unifi.ft.rehearsal.exceptions.ScheduleNotFoundException;
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

		DateTime start = new DateTime(new DateTime(2121, 12, 12, 12, 12, 12));
		DateTime end = start.plusHours(Scheduler.HOUR_DURATION).plusMinutes(Scheduler.MINUTE_DURATION);
		Band b = new Band("ProvaBand0", "ProvaPw0");

		Schedule result = scheduler.initAndSaveSchedule(b, start, RehearsalRoom.FIRSTROOM);
		assertNotNull(result);
		assertEquals(start, result.getStartDate());
		assertEquals(end, result.getEndDate());
		assertEquals(b, result.getBand());
		assertEquals(RehearsalRoom.FIRSTROOM, result.getRoom());
		verify(repository,times(1)).findAll();
		verify(repository,times(1)).save(result);
	}
	
	@Test
	public void testCreateScheduleSameTimeDifferentRooms() {
		when(repository.findAll()).thenReturn(notEmptyList(1));

		DateTime start = new DateTime(2121, 12, 12, 12, 12, 12);
		DateTime end = start.plusHours(Scheduler.HOUR_DURATION).plusMinutes(Scheduler.MINUTE_DURATION);
		Band b = new Band("ProvaBand0", "ProvaPw0");
		
		Schedule result = scheduler.initAndSaveSchedule(b, start, RehearsalRoom.SECONDROOM);
		assertNotNull(result);
		assertEquals(start, result.getStartDate());
		assertEquals(end, result.getEndDate());
		assertEquals(b, result.getBand());
		assertEquals(RehearsalRoom.SECONDROOM, result.getRoom());
		verify(repository,times(1)).findAll();
		verify(repository,times(1)).save(result);
	}
	
	@Test
	public void testCreateScehduleSameRoomDifferentTimes() {
		when(repository.findAll()).thenReturn(notEmptyList(1));

		DateTime start = new DateTime(2121, 12, 12, 16, 12, 12);
		DateTime end = start.plusHours(Scheduler.HOUR_DURATION).plusMinutes(Scheduler.MINUTE_DURATION);
		Band b = new Band("ProvaBand0", "ProvaPw0");

		Schedule result = scheduler.initAndSaveSchedule(b, start, RehearsalRoom.FIRSTROOM);
		assertNotNull(result);
		assertEquals(start, result.getStartDate());
		assertEquals(end, result.getEndDate());
		assertEquals(b, result.getBand());
		assertEquals(RehearsalRoom.FIRSTROOM, result.getRoom());
		verify(repository,times(1)).findAll();
		verify(repository,times(1)).save(result);
	}

	@Test(expected = InvalidTimeException.class)
	public void testCreateScheduleWhenItIsTooLate() {
		when(repository.findAll()).thenReturn(notEmptyList(1));

		DateTime start = new DateTime(new Date());
		Band b = new Band("ProvaBand0", "ProvaPw0");

		Schedule result = scheduler.initAndSaveSchedule(b, start, RehearsalRoom.FIRSTROOM);
		assertNull(result);
	}
	
	@Test(expected = RoomNotFreeException.class)
	public void testCreateScheduleWhenAnotherExistsOnStartDate() {
		when(repository.findAll()).thenReturn(notEmptyList(1));

		DateTime start = new DateTime(2121, 12, 12, 12, 12, 12);
		Band b = new Band("ProvaBand0", "ProvaPw0");

		Schedule result = scheduler.initAndSaveSchedule(b, start, RehearsalRoom.FIRSTROOM);
		assertNull(result);
	}
	
	@Test(expected = RoomNotFreeException.class)
	public void testCreateScheduleWhenAnotherExistsOnEndDate() {
		when(repository.findAll()).thenReturn(notEmptyList(1));

		DateTime start = new DateTime(2121, 12, 12, 10, 12, 12);
		Band b = new Band("ProvaBand0", "ProvaPw0");

		Schedule result = scheduler.initAndSaveSchedule(b, start, RehearsalRoom.FIRSTROOM);
		assertNull(result);
	}
	
	@Test
	public void testDeleteSchedule() {
		when(repository.findAll()).thenReturn(notEmptyList(1));
		
		DateTime start = new DateTime(2121, 12, 12, 12, 12, 12);
		Band b = new Band("ProvaBand0", "ProvaPw0");
		
		Schedule result = scheduler.deleteSchedule(b, start, RehearsalRoom.FIRSTROOM);
		assertNotNull(result);
		verify(repository,times(1)).findAll();
		verify(repository,times(1)).delete(result);
	}
	
	@Test(expected = ScheduleNotFoundException.class)
	public void testDeleteScheduleWhenItIsNotFound() {
		when(repository.findAll()).thenReturn(notEmptyList(1));

		DateTime start = new DateTime(2121, 12, 12, 10, 12, 12);
		Band b = new Band("ProvaBand0", "ProvaPw0");
		
		Schedule result = scheduler.deleteSchedule(b, start, RehearsalRoom.FIRSTROOM);
		assertNull(result);
		verify(repository,times(1)).findAll();
	}
	
	@Test
	public void testFindSchedulesByBand() {
		when(repository.findAll()).thenReturn(notEmptyList(1));

		Band b = new Band("ProvaBand0", "ProvaPw0");
		
		List<Schedule> result = scheduler.findSchedulesByBand(b);
		assertNotNull(result);
		assertEquals(1,result.size());
		assertEquals(result.get(0).getBand(),b);
		verify(repository,times(1)).findAll();
	}
	
	@Test
	public void testFindSchedulesByBandWhenThereIsNot() {
		when(repository.findAll()).thenReturn(notEmptyList(1));

		Band b = new Band("ProvaBand1", "ProvaPw1");
		
		List<Schedule> result = scheduler.findSchedulesByBand(b);
		assertNotNull(result);
		assertEquals(0,result.size());
		verify(repository,times(1)).findAll();
	}

	@Test
	public void testFindSchedulesByDate() {
		when(repository.findAll()).thenReturn(notEmptyList(1));
		
		List<Schedule> result = scheduler.findSchedulesByDate(2121, 12, 12);
		
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(result.get(0).getStartDate().getYear(), 2121);
		assertEquals(result.get(0).getStartDate().getDayOfMonth(), 12);
		assertEquals(result.get(0).getStartDate().getMonthOfYear(), 12);
		verify(repository,times(1)).findAll();
	}
	
	@Test
	public void testFindSchedulesByDateWrongDay() {
		when(repository.findAll()).thenReturn(notEmptyList(1));
		
		List<Schedule> result = scheduler.findSchedulesByDate(2121, 12, 10);
		
		assertNotNull(result);
		assertEquals(0, result.size());
		verify(repository,times(1)).findAll();
	}
	
	@Test
	public void testFindSchedulesByDateWrongMonth() {
		when(repository.findAll()).thenReturn(notEmptyList(1));
		
		List<Schedule> result = scheduler.findSchedulesByDate(2121, 10, 12);
		
		assertNotNull(result);
		assertEquals(0, result.size());
		verify(repository,times(1)).findAll();
	}

	@Test
	public void testFindSchedulesByDateWrongYear() {
		when(repository.findAll()).thenReturn(notEmptyList(1));
		
		List<Schedule> result = scheduler.findSchedulesByDate(2120, 12, 12);
		
		assertNotNull(result);
		assertEquals(0, result.size());
		verify(repository,times(1)).findAll();
	}
	
	@Test
	public void testFindSchedulesByRoom() {
		when(repository.findAll()).thenReturn(notEmptyList(1));
		
		List<Schedule> result = scheduler.findSchedulesByRoom(RehearsalRoom.FIRSTROOM);
		
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(RehearsalRoom.FIRSTROOM, result.get(0).getRoom());
		verify(repository,times(1)).findAll();
	}
	
	@Test
	public void testFindSchedulesByRoomWhenItIsTooLate() {
		when(repository.findAll()).thenReturn(notEmptySpecialList(3));
		
		List<Schedule> result = scheduler.findSchedulesByRoom(RehearsalRoom.FIRSTROOM);
		
		assertNotNull(result);
		assertEquals(0, result.size());
		verify(repository, times(1)).findAll();
	}
	
	@Test
	public void testFindSchedulesByRoomWhenRoomIsEmpty() {
		when(repository.findAll()).thenReturn(notEmptyList(3));
		
		List<Schedule> result = scheduler.findSchedulesByRoom(RehearsalRoom.SECONDROOM);
		
		assertNotNull(result);
		assertEquals(0, result.size());
		verify(repository, times(1)).findAll();
	}
	
	private List<Schedule> notEmptyList(int size) {
		List<Schedule> result = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			Band b = new Band("ProvaBand"+i, "ProvaPw"+i);
			DateTime start = new DateTime(2121, 12, 12, 12, 12+i, 12);
			DateTime end = start.plusHours(Scheduler.HOUR_DURATION).plusMinutes(Scheduler.MINUTE_DURATION);
			Schedule s = new Schedule(b, start, end, RehearsalRoom.FIRSTROOM);
			result.add(s);
		}
		return result;
	}
	
	private List<Schedule> notEmptySpecialList(int size) {
		List<Schedule> result = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			Band b = new Band("ProvaBand"+i, "ProvaPw"+i);
			DateTime start = new DateTime(2001, 12, 12, 12, 12+3*i, 12);
			DateTime end = start.plusHours(Scheduler.HOUR_DURATION).plusMinutes(Scheduler.MINUTE_DURATION);
			Schedule s = new Schedule(b, start, end, RehearsalRoom.FIRSTROOM);
			result.add(s);
		}
		return result;
	}

}
