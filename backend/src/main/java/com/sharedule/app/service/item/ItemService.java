package com.sharedule.app.service.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sharedule.app.model.item.Item;
import com.sharedule.app.model.user.Users;
import com.sharedule.app.dto.CreateItemDTO;
import com.sharedule.app.exception.NotFoundException;
import com.sharedule.app.exception.BackendErrorException;
import com.sharedule.app.repository.item.ItemRepo;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

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

    public List<Item> getItemsByUser(Users user) {
        return repo.findByUser(user);
    }

    public Item viewItem(String itemId) throws BackendErrorException {
        try {
            Item itemToBeViewed = repo.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found"));
            return itemToBeViewed;
        } catch (NotFoundException nfe) {
            throw new BackendErrorException(nfe);
        }
    }

    public List<Item> searchItems(String query) throws BackendErrorException {
        try {
            List<Item> itemsFound = repo.findByItemNameContainingIgnoreCase(query);
            if (itemsFound.isEmpty()) {
                throw new NotFoundException("The item you requested could not be found");
            }
            return itemsFound;
        } catch (NotFoundException nfe) {
            throw new BackendErrorException(nfe);
        }
    }

    public List<Item> availableItems() throws BackendErrorException {
        try {
            List<Item> availableItemsFound = repo.findByItemAvailableTrue();
            if (availableItemsFound.isEmpty()) {
                throw new NotFoundException("There are no available items.");
            }
            return availableItemsFound;
        } catch (NotFoundException nfe) {
            throw new BackendErrorException(nfe);
        }
    }

    public List<Item> searchAvailableItems(String query) throws BackendErrorException {
        try {
            List<Item> availableItemsFound = repo.findByItemNameContainingIgnoreCaseAndItemAvailableTrue(query);
            if (availableItemsFound.isEmpty()) {
                throw new NotFoundException("The item you requested could not be found");
            }
            return availableItemsFound;
        } catch (NotFoundException nfe) {
            throw new BackendErrorException(nfe);
        }
    }
}