package com.sharedule.app.repository.timeslot;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.sharedule.app.model.timeslot.Timeslot;

public interface TimeslotRepo extends MongoRepository<Timeslot, String> {
    Timeslot findByTransaction_Id(String transactionId);
}
