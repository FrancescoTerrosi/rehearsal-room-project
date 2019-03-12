package org.unifi.ft.rehearsal.repository.mongo;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.junit4.SpringRunner;
import org.unifi.ft.rehearsal.model.BandDetails;
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
		Schedule toSave = createSchedule("bandName","bandPw");
		repository.save(toSave);
		List<Schedule> result = mongoOps.findAll(Schedule.class);
		assertEquals(1, result.size());
		assertThat(result.get(0), instanceOf(Schedule.class));
		assertEquals(toSave, result.get(0));
	}
	
	@Test
	public void testFindAll() {
		Schedule s1 = createSchedule("bandName1", "bandPw1");
		Schedule s2 = createSchedule("bandName2", "bandPw2");
		Schedule s3 = createSchedule("bandName3", "bandPw3");

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
	public void testDelete() {
		Schedule s1 = createSchedule("band1", "pw1");
		Schedule s2 = createSchedule("band2", "pw2");
		Schedule s3 = createSchedule("band3", "pw3");

		mongoOps.save(s1);
		mongoOps.save(s2);
		mongoOps.save(s3);

		repository.delete(s1);
		
		List<Schedule> result = mongoOps.findAll(Schedule.class);
		
		assertEquals(2, result.size());
		for (Schedule s : result) {
			assertNotEquals(s, s1);
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testDeleteWhenThereIsNot() {
		Schedule s1 = createSchedule("bandName1", "bandPw1");
		Schedule s2 = createSchedule("bandName2", "bandPw2");
		Schedule s3 = createSchedule("bandName3", "bandPw3");

		mongoOps.save(s2);
		mongoOps.save(s3);

		repository.delete(s1);
	}
	
	private Schedule createSchedule(String bandName, String bandPw) {
		BandDetails band = new BandDetails(bandName, bandPw);
		Schedule result = new Schedule(band, new DateTime(), new DateTime(), RehearsalRoom.FIRSTROOM);
		return result;
	}
}
