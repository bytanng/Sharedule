package com.sharedule.app.model.transaction;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.sharedule.app.model.item.Item;
import com.sharedule.app.model.timeslot.Timeslot;
import com.sharedule.app.model.user.Users;

import org.springframework.data.mongodb.core.mapping.DBRef;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    private String id;

    private String transactionName;

    private String buyerId;

    private String transactionLocation;

    @DBRef
    private Users seller; // Reference to the user who created the transaction

    @DBRef
    private Users buyer;

    @DBRef
    private Item item; // Reference to the item being transacted

    @DBRef
    private Timeslot timeslot;
}
