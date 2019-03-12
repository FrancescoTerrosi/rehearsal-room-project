package org.unifi.ft.rehearsal.repository.mongo;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.junit4.SpringRunner;
import org.unifi.ft.rehearsal.model.BandDetails;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BandDetailsMongoRepositoryIT {

	@Autowired 
	private IBandDetailsMongoRepository repository;

	@Autowired 
	private MongoOperations mongoOps;

	@Before
	public void setUp() {
		mongoOps.dropCollection(BandDetails.class);
	}

	@Test
	public void testSave() {
		BandDetails toSave = new BandDetails("bandName","bandPw");
		repository.save(toSave);
		List<BandDetails> result = mongoOps.findAll(BandDetails.class);
		assertEquals(1, result.size());
		assertThat(result.get(0), instanceOf(BandDetails.class));
		assertEquals(toSave, result.get(0));
	}
	
	@Test
	public void testFindAll() {
		BandDetails b1 = new BandDetails("bandName1", "bandPw1");
		BandDetails b2 = new BandDetails("bandName2", "bandPw2");
		BandDetails b3 = new BandDetails("bandName3", "bandPw3");

		mongoOps.save(b1);
		mongoOps.save(b2);
		mongoOps.save(b3);

		List<BandDetails> result = repository.findAll();
		assertEquals(3, result.size());
		assertThat(result.get(0), instanceOf(BandDetails.class));
		assertThat(result.get(1), instanceOf(BandDetails.class));
		assertThat(result.get(2), instanceOf(BandDetails.class));
		assertEquals(b1, result.get(0));
		assertEquals(b2, result.get(1));
		assertEquals(b3, result.get(2));
	}
	
	@Test
	public void testFindAllWhenEmpty() {
		List<BandDetails> result = repository.findAll();
		assertEquals(0, result.size());
	}
	
}
