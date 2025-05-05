package com.sharedule.app.model.item;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import com.sharedule.app.model.user.Users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    @Id
    private String id;
    
    private String itemName;
    private String itemDescription;
    private double itemPrice;
    private long itemStock;
    private boolean itemAvailable;
    private String itemImage;
    
    @DBRef
    private Users user;  // Reference to the user who created the item
}
