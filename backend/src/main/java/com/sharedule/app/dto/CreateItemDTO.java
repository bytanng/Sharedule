package com.sharedule.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateItemDTO {
    private String itemName;
    private String itemDescription;
    private double itemPrice;
    private long itemStock;
    private boolean itemAvailable;
    private String itemImage;
}
