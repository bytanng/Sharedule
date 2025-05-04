package com.sharedule.app.repository.timeslot;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.sharedule.app.model.timeslot.Timeslot;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;

public interface TimeslotRepo extends MongoRepository<Timeslot, String> {
    Timeslot findByTransaction_Id(String transactionId);

    @Query("""
        {
         "_id": { "$ne": ?4 },
         "$or": [
             { "transaction.buyer._id": ?0 },
             { "transaction.seller._id": ?1 }
         ],
         "startDateTime": { "$lt": ?3 },
         "endDateTime": { "$gt": ?2 }
        }
    """)
    List<Timeslot> findConflictingTimeslots(
            String buyerId,
            String sellerId,
            Date start,
            Date end,
            String excludeTimeslotId
    );
}
