package org.unifi.ft.rehearsal.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.unifi.ft.rehearsal.model.Schedule;

@Repository("ScheduleRepository")
public interface IScheduleMongoRepository extends MongoRepository<Schedule, String> {

}
