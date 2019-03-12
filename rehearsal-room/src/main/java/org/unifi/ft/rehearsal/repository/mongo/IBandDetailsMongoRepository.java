package org.unifi.ft.rehearsal.repository.mongo;

import java.math.BigInteger;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.unifi.ft.rehearsal.model.BandDetails;

@Repository("BandRepository")
public interface IBandDetailsMongoRepository extends MongoRepository<BandDetails, BigInteger> {

}
