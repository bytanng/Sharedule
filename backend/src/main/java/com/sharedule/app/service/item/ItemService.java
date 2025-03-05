package com.sharedule.app.service.item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sharedule.app.model.item.Item;
import com.sharedule.app.model.user.Users;
import com.sharedule.app.dto.CreateItemDTO;
import com.sharedule.app.repository.item.ItemRepo;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ItemService {
    @Autowired
    private ItemRepo repo;

    public Item createItem(CreateItemDTO itemDTO, Users user) {
        // Validate required fields
        if (itemDTO.getItemName() == null || itemDTO.getItemName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item name is required");
        }
        if (itemDTO.getItemDescription() == null || itemDTO.getItemDescription().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item description is required");
        }
        
        // Validate price and stock
        if (itemDTO.getItemPrice() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price cannot be negative");
        }
        if (itemDTO.getItemStock() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock cannot be negative");
        }

        // Validate image URL format if provided
        if (itemDTO.getItemImage() != null && !itemDTO.getItemImage().trim().isEmpty() 
            && !itemDTO.getItemImage().matches("^(http|https)://.*$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid image URL format");
        }

        Item item = new Item();
        item.setItemName(itemDTO.getItemName().trim());
        item.setItemDescription(itemDTO.getItemDescription().trim());
        item.setItemPrice(itemDTO.getItemPrice());
        item.setItemStock(itemDTO.getItemStock());
        item.setItemAvailable(itemDTO.isItemAvailable());
        item.setItemImage(itemDTO.getItemImage() != null ? itemDTO.getItemImage().trim() : "");
        item.setUser(user);
        
        return repo.save(item);
    }
}