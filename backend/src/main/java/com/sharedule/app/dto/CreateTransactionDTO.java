package com.sharedule.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransactionDTO {
    private String transactionName;
    private String buyerId;
    private String timeslotId;
    private String transactionLocation;
}
