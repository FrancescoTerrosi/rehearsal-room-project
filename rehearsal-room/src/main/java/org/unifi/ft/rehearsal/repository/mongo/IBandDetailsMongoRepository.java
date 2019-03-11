package org.unifi.ft.rehearsal.repository.mongo;

import java.math.BigInteger;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

@Repository("BandRepository")
public interface IBandDetailsMongoRepository extends MongoRepository<UserDetails, BigInteger> {

}
