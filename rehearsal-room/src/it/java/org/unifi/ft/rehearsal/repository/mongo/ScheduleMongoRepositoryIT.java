package org.unifi.ft.rehearsal.repository.mongo;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.junit4.SpringRunner;
import org.unifi.ft.rehearsal.model.RehearsalRoom;
import org.unifi.ft.rehearsal.model.Schedule;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ScheduleMongoRepositoryIT {

	@Autowired 
	private IScheduleMongoRepository repository;

	@Autowired 
	private MongoOperations mongoOps;

	@Before
	public void setUp() {
		mongoOps.dropCollection(Schedule.class);
	}

	@Test
	public void testSave() {
		Schedule toSave = createSchedule("bandName", "0");
		repository.save(toSave);
		List<Schedule> result = mongoOps.findAll(Schedule.class);
		assertEquals(1, result.size());
		assertThat(result.get(0), instanceOf(Schedule.class));
		assertEquals(toSave, result.get(0));
	}
	
	@Test
	public void testFindById() {
		Schedule s1 = createSchedule("bandName1", "1");

		mongoOps.save(s1);
		
		Optional<Schedule> result = repository.findById(new BigInteger("1"));
		assertTrue(result.isPresent());
		assertThat(result.get(), instanceOf(Schedule.class));
		assertEquals(s1, result.get());
	}
	
	@Test
	public void testFindByIdWhenThereIsNot() {
		Schedule s1 = createSchedule("bandName1", "1");

		mongoOps.save(s1);
		
		Optional<Schedule> result = repository.findById(new BigInteger("0"));
		assertFalse(result.isPresent());
	}
	
	@Test
	public void testFindAll() {
		Schedule s1 = createSchedule("bandName1", "1");
		Schedule s2 = createSchedule("bandName2", "2");
		Schedule s3 = createSchedule("bandName3", "3");

		mongoOps.save(s1);
		mongoOps.save(s2);
		mongoOps.save(s3);

		List<Schedule> result = repository.findAll();
		assertEquals(3, result.size());
		assertThat(result.get(0), instanceOf(Schedule.class));
		assertThat(result.get(1), instanceOf(Schedule.class));
		assertThat(result.get(2), instanceOf(Schedule.class));
		assertEquals(s1, result.get(0));
		assertEquals(s2, result.get(1));
		assertEquals(s3, result.get(2));
	}
	
	@Test
	public void testFindAllWhenEmpty() {
		List<Schedule> result = repository.findAll();
		assertEquals(0, result.size());
	}

	@Test
	public void testDeleteById() {
		Schedule s1 = createSchedule("band1", "1");
		Schedule s2 = createSchedule("band2", "2");
		Schedule s3 = createSchedule("band3", "3");

		mongoOps.save(s1);
		mongoOps.save(s2);
		mongoOps.save(s3);

		repository.deleteById(new BigInteger("1"));
		
		List<Schedule> result = mongoOps.findAll(Schedule.class);
		
		assertEquals(2, result.size());
		for (Schedule s : result) {
			assertNotEquals(s, s1);
		}
	}
	
	@Test
	public void testDeleteWhenThereIsNot() {
		Schedule s2 = createSchedule("bandName2", "2");
		Schedule s3 = createSchedule("bandName3", "3");

		mongoOps.save(s2);
		mongoOps.save(s3);
		
		assertEquals(2, repository.count());
		repository.deleteById(new BigInteger("1"));
		assertEquals(2, repository.count());
	}
	
	private Schedule createSchedule(String bandName, String id) {
		Schedule result = new Schedule(bandName, new DateTime(), new DateTime(), RehearsalRoom.FIRSTROOM);
		BigInteger sId = new BigInteger(id);
		result.setId(sId);
		return result;
	}
}
