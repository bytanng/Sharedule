package com.sharedule.app.model.timeslot;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.sharedule.app.model.transaction.Transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "timeslots")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Timeslot {
    @Id
    private String id;

    private Date startDateTime;

    private Date endDateTime;

    @DBRef
    Transaction transaction; // Reference to the transaction associated with this timeslot
}
