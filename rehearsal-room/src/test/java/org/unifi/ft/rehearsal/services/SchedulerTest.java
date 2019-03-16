package org.unifi.ft.rehearsal.services;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.unifi.ft.rehearsal.services.Scheduler;

import org.unifi.ft.rehearsal.exceptions.InvalidTimeException;
import org.unifi.ft.rehearsal.exceptions.RoomNotFreeException;
import org.unifi.ft.rehearsal.exceptions.ScheduleNotFoundException;
import org.unifi.ft.rehearsal.model.RehearsalRoom;
import org.unifi.ft.rehearsal.model.Schedule;
import org.unifi.ft.rehearsal.repository.mongo.IScheduleMongoRepository;

public class SchedulerTest {

	private IScheduleMongoRepository repository;
	private Scheduler scheduler;

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
		String b = "ProvaBand0";

		Schedule result = scheduler.initAndSaveSchedule(b, start, RehearsalRoom.FIRSTROOM);
		assertNotNull(result);
		assertEquals(start, result.getStartDate());
		assertEquals(end, result.getEndDate());
		assertEquals(b, result.getBand());
		assertEquals(RehearsalRoom.FIRSTROOM, result.getRoom());
		verify(repository, times(1)).findAll();
		verify(repository, times(1)).save(result);
	}

	@Test
	public void testCreateScheduleSameTimeDifferentRooms() {
		when(repository.findAll()).thenReturn(notEmptyList(1));

		DateTime start = new DateTime(2121, 12, 12, 12, 12, 12);
		DateTime end = start.plusHours(Scheduler.HOUR_DURATION).plusMinutes(Scheduler.MINUTE_DURATION);
		String b = "ProvaBand0";

		Schedule result = scheduler.initAndSaveSchedule(b, start, RehearsalRoom.SECONDROOM);
		assertNotNull(result);
		assertEquals(start, result.getStartDate());
		assertEquals(end, result.getEndDate());
		assertEquals(b, result.getBand());
		assertEquals(RehearsalRoom.SECONDROOM, result.getRoom());
		verify(repository, times(1)).findAll();
		verify(repository, times(1)).save(result);
	}

	@Test
	public void testCreateScheduleSameRoomDifferentTimes() {
		when(repository.findAll()).thenReturn(notEmptyList(1));

		DateTime start = new DateTime(2121, 12, 12, 16, 12, 12);
		DateTime end = start.plusHours(Scheduler.HOUR_DURATION).plusMinutes(Scheduler.MINUTE_DURATION);
		String b = "ProvaBand0";

		Schedule result = scheduler.initAndSaveSchedule(b, start, RehearsalRoom.FIRSTROOM);
		assertNotNull(result);
		assertEquals(start, result.getStartDate());
		assertEquals(end, result.getEndDate());
		assertEquals(b, result.getBand());
		assertEquals(RehearsalRoom.FIRSTROOM, result.getRoom());
		verify(repository, times(1)).findAll();
		verify(repository, times(1)).save(result);
	}

	@Test(expected = InvalidTimeException.class)
	public void testCreateScheduleWhenItIsTooLate() {
		DateTime start = new DateTime(new Date());
		String b = "ProvaBand0";

		Schedule result = scheduler.initAndSaveSchedule(b, start, RehearsalRoom.FIRSTROOM);
		assertNull(result);
	}

	@Test(expected = RoomNotFreeException.class)
	public void testCreateScheduleWhenAnotherExistsOnStartDate() {
		when(repository.findAll()).thenReturn(notEmptyList(1));

		DateTime start = new DateTime(2121, 12, 12, 12, 12, 12);
		String b = "ProvaBand0";

		Schedule result = scheduler.initAndSaveSchedule(b, start, RehearsalRoom.FIRSTROOM);
		assertNull(result);
	}

	@Test(expected = RoomNotFreeException.class)
	public void testCreateScheduleWhenAnotherExistsOnEndDate() {
		when(repository.findAll()).thenReturn(notEmptyList(1));

		DateTime start = new DateTime(2121, 12, 12, 10, 12, 12);
		String b = "ProvaBand0";

		Schedule result = scheduler.initAndSaveSchedule(b, start, RehearsalRoom.FIRSTROOM);
		assertNull(result);
	}

	@Test
	public void testDeleteSchedule() {
		String b = "ProvaBand0";
		DateTime start = new DateTime(2121, 12, 12, 12, 12, 12);
		DateTime end = start.plusHours(Scheduler.HOUR_DURATION).plusMinutes(Scheduler.MINUTE_DURATION);
		Schedule s = new Schedule(b, start, end, RehearsalRoom.FIRSTROOM);
		s.setId(new BigInteger("0"));
		
		BigInteger id = new BigInteger("0");

		when(repository.findAll()).thenReturn(notEmptyList(1));
		when (repository.findById(id)).thenReturn(Optional.of(s));
		
		Schedule result = scheduler.deleteSchedule(id);
		assertNotNull(result);
		verify(repository, times(1)).findById(id);
		verify(repository, times(1)).deleteById(id);
	}

	@Test(expected = ScheduleNotFoundException.class)
	public void testDeleteScheduleWhenItIsNotFound() {
		when(repository.findAll()).thenReturn(notEmptyList(1));

		BigInteger id = new BigInteger("1");

		Schedule result = scheduler.deleteSchedule(id);
		assertNull(result);
		verify(repository, times(1)).findById(id);
		verify(repository, times(1)).deleteById(id);
	}

	@Test
	public void testFindSchedulesByBandName() {
		when(repository.findAll()).thenReturn(notEmptyList(1));

		String b = "ProvaBand0";

		List<Schedule> result = scheduler.findSchedulesByBand(b);
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(b, result.get(0).getBand());
		verify(repository, times(1)).findAll();
	}

	@Test
	public void testFindSchedulesByBandWhenThereIsNot() {
		when(repository.findAll()).thenReturn(notEmptyList(1));

		String b = "ProvaBand1";

		List<Schedule> result = scheduler.findSchedulesByBand(b);
		assertNotNull(result);
		assertEquals(0, result.size());
		verify(repository, times(1)).findAll();
	}

	@Test
	public void testFindSchedulesByDate() {
		when(repository.findAll()).thenReturn(notEmptyList(1));

		List<Schedule> result = scheduler.findSchedulesByDate(2121, 12, 12);

		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(2121, result.get(0).getStartDate().getYear());
		assertEquals(12, result.get(0).getStartDate().getDayOfMonth());
		assertEquals(12, result.get(0).getStartDate().getMonthOfYear());
		verify(repository, times(1)).findAll();
	}

	@Test
	public void testFindSchedulesByDateWrongDay() {
		when(repository.findAll()).thenReturn(notEmptyList(1));

		List<Schedule> result = scheduler.findSchedulesByDate(2121, 12, 10);

		assertNotNull(result);
		assertEquals(0, result.size());
		verify(repository, times(1)).findAll();
	}

	@Test
	public void testFindSchedulesByDateWrongMonth() {
		when(repository.findAll()).thenReturn(notEmptyList(1));

		List<Schedule> result = scheduler.findSchedulesByDate(2121, 10, 12);

		assertNotNull(result);
		assertEquals(0, result.size());
		verify(repository, times(1)).findAll();
	}

	@Test
	public void testFindSchedulesByDateWrongYear() {
		when(repository.findAll()).thenReturn(notEmptyList(1));

		List<Schedule> result = scheduler.findSchedulesByDate(2120, 12, 12);

		assertNotNull(result);
		assertEquals(0, result.size());
		verify(repository, times(1)).findAll();
	}

	@Test
	public void testFindSchedulesByRoom() {
		when(repository.findAll()).thenReturn(notEmptyList(1));

		List<Schedule> result = scheduler.findSchedulesByRoom(RehearsalRoom.FIRSTROOM);

		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(RehearsalRoom.FIRSTROOM, result.get(0).getRoom());
		verify(repository, times(1)).findAll();
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
			String b = "ProvaBand" + i;
			DateTime start = new DateTime(2121, 12, 12, 12, 12 + i, 12);
			DateTime end = start.plusHours(Scheduler.HOUR_DURATION).plusMinutes(Scheduler.MINUTE_DURATION);
			Schedule s = new Schedule(b, start, end, RehearsalRoom.FIRSTROOM);
			s.setId(new BigInteger(String.valueOf(i)));
			result.add(s);
		}
		return result;
	}

	private List<Schedule> notEmptySpecialList(int size) {
		List<Schedule> result = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			String b = "ProvaBand" + i;
			DateTime start = new DateTime(2001, 12, 12, 12, 12 + 3 * i, 12);
			DateTime end = start.plusHours(Scheduler.HOUR_DURATION).plusMinutes(Scheduler.MINUTE_DURATION);
			Schedule s = new Schedule(b, start, end, RehearsalRoom.FIRSTROOM);
			result.add(s);
		}
		return result;
	}

}
