package com.sharedule.app.service.item;


import com.sharedule.app.dto.CreateItemDTO;
import com.sharedule.app.dto.EditItemDTO;
import com.sharedule.app.exception.BackendErrorException;
import com.sharedule.app.exception.ExistsInRepoException;
import com.sharedule.app.exception.NotFoundException;
import com.sharedule.app.model.user.AppUsers;
import com.sharedule.app.model.user.Users;
import com.sharedule.app.repository.user.UserRepo;
import com.sharedule.app.model.item.Item;
import com.sharedule.app.repository.item.ItemRepo;
import com.sharedule.app.util.user.ValidationUtil;
import com.sharedule.app.service.user.JWTService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ItemServiceTest {

    @Mock
    private ItemRepo itemRepo;

    @Mock
    private JWTService jwtService;

    @Mock
    private AuthenticationManager authManager;

    @InjectMocks
    private ItemService itemService;

    private AppUsers appUser;

    private CreateItemDTO createItemDTO;
    private EditItemDTO editItemDTO;

    Item item1 = new Item();
    Item item2 = new Item();
    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);

        createItemDTO = new CreateItemDTO();
        createItemDTO.setItemName("Item Name 1");
        createItemDTO.setItemDescription("Item Description 1");
        createItemDTO.setItemPrice(25.0);
        createItemDTO.setItemStock(1);
        createItemDTO.setItemAvailable(true);
        createItemDTO.setItemImage("https://sharedule.s3.us-east-1.amazonaws.com/1742734103919_johncena.jpg");

        editItemDTO = new EditItemDTO();
        editItemDTO.setItemName("Item Name Change");
        editItemDTO.setItemDescription("Item Description Change");
        editItemDTO.setItemPrice(2111.0);
        editItemDTO.setItemStock(5L);
        editItemDTO.setItemAvailable(false);
        editItemDTO.setItemImage("https://sharedule.s3.us-east-1.amazonaws.com/changed_johncena.jpg");

        appUser = new AppUsers();
        appUser.setUsername("testUser");
        appUser.setEmail("test@hotmail.com.com");
        appUser.setPassword("hashedPassword");
        appUser.setRole("USER");


        item1.setItemName("Item Name One");
        item1.setItemDescription("Item Description One");
        item1.setItemPrice(25.0);
        item1.setItemStock(1L);
        item1.setItemAvailable(true);
        item1.setUser(appUser);


        item2.setItemName("Item Name Two");
        item2.setItemDescription("Item Description Two");
        item2.setItemPrice(50.0);
        item2.setItemStock(2L);
        item2.setItemAvailable(true);
        item2.setUser(appUser);
    }

    @Test
    void testCreateItem_Success() {
        Item savedItem = new Item();
        savedItem.setItemName(createItemDTO.getItemName());
        savedItem.setItemDescription(createItemDTO.getItemDescription());
        savedItem.setItemPrice(createItemDTO.getItemPrice());
        savedItem.setItemStock(createItemDTO.getItemStock());
        savedItem.setItemAvailable(createItemDTO.isItemAvailable());
        savedItem.setItemImage(createItemDTO.getItemImage());
        savedItem.setUser(appUser);

        when(itemRepo.save(savedItem)).thenReturn(savedItem);

        Item result = itemService.createItem(createItemDTO, appUser);

        verify(itemRepo).save(savedItem);

        assertEquals(savedItem.getItemName(), result.getItemName());
        assertEquals(savedItem.getItemDescription(), result.getItemDescription());
        assertEquals(savedItem.getItemPrice(), result.getItemPrice());
        assertEquals(savedItem.getItemStock(), result.getItemStock());
        assertEquals(savedItem.isItemAvailable(), result.isItemAvailable());
        assertEquals(savedItem.getItemImage(), result.getItemImage());
        assertEquals(savedItem.getUser(), result.getUser());
    }

    @Test
    void testUpdateItem_Success() {
        // Act
        Item existingItem = new Item();
        existingItem.setItemName(editItemDTO.getItemName());
        existingItem.setItemDescription(editItemDTO.getItemDescription());
        existingItem.setItemPrice(editItemDTO.getItemPrice());
        existingItem.setItemStock(editItemDTO.getItemStock());
        existingItem.setItemAvailable(editItemDTO.getItemAvailable());
        existingItem.setItemImage(editItemDTO.getItemImage());
        when(itemRepo.findById(existingItem.getId())).thenReturn(Optional.of(existingItem));
        when(itemRepo.save(existingItem)).thenReturn(existingItem);
        Item updatedItem = itemService.updateItem(existingItem, editItemDTO);

        assertEquals(editItemDTO.getItemName(), updatedItem.getItemName());
        assertEquals(editItemDTO.getItemDescription(), updatedItem.getItemDescription());
        assertEquals(editItemDTO.getItemPrice(), updatedItem.getItemPrice());
        assertEquals(editItemDTO.getItemStock(), updatedItem.getItemStock());
        assertEquals(editItemDTO.getItemAvailable(), updatedItem.isItemAvailable());
        assertEquals(editItemDTO.getItemImage(), updatedItem.getItemImage());

        verify(itemRepo).save(existingItem);
    }


    @Test
    void testGetItemsByUser_Success() {
        // Arrange


        List<Item> expectedItems = Arrays.asList(item1, item2);

        when(itemRepo.findByUser(appUser)).thenReturn(expectedItems);

        // Act
        List<Item> result = itemService.getItemsByUser(appUser);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Item Name One", result.get(0).getItemName());
        assertEquals("Item Name Two", result.get(1).getItemName());
        verify(itemRepo, times(1)).findByUser(appUser);
    }

    @Test
    void testGetItem_Success() throws BackendErrorException {
        when(itemRepo.save(item1)).thenReturn(item1);

        when(itemRepo.findById("1")).thenReturn(Optional.of(item1));

        Item result = itemService.getItem("1");

        assertNotNull(result);
        assertEquals("Item Name One", result.getItemName());
        verify(itemRepo, times(1)).findById("1");
    }


    @Test
    void testGetItem_NotFound() {
        // Arrange: Simulate that the item does not exist
        String itemId = "99";
        when(itemRepo.findById(itemId)).thenReturn(Optional.empty());

        // Act & Assert: Expect BackendErrorException when calling getItem
        BackendErrorException exception = assertThrows(BackendErrorException.class, () -> {
            itemService.getItem(itemId);
        });

        // Verify that the exception message is correct
        assertEquals("Item not found",exception.getMessage());

        // Verify that findById was called once
        verify(itemRepo, times(1)).findById(itemId);
    }




    @Test
    void testSearchItems_Success() throws BackendErrorException {
        when(itemRepo.findByItemNameContainingIgnoreCase("Item")).thenReturn(Arrays.asList(item1, item2));

        List<Item> result = itemService.searchItems("Item");

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(itemRepo, times(1)).findByItemNameContainingIgnoreCase("Item");
    }

    @Test
    void testSearchItems_NotFound() {
        when(itemRepo.findByItemNameContainingIgnoreCase("NonExistent")).thenReturn(List.of());

        BackendErrorException exception = assertThrows(BackendErrorException.class, () -> {
            itemService.searchItems("NonExistent");
        });

        assertEquals("The item you requested could not be found",exception.getMessage());
        verify(itemRepo, times(1)).findByItemNameContainingIgnoreCase("NonExistent");
    }

    @Test
    void testGetProducts_Success() throws BackendErrorException {
        when(itemRepo.findByItemAvailableTrue()).thenReturn(List.of(item1));

        List<Item> result = itemService.getProducts();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isItemAvailable());
        verify(itemRepo, times(1)).findByItemAvailableTrue();
    }

    @Test
    void testGetProducts_NotFound() {
        when(itemRepo.findByItemAvailableTrue()).thenReturn(List.of());

        BackendErrorException exception = assertThrows(BackendErrorException.class, () -> {
            itemService.getProducts();
        });

        assertEquals("There are no available items.", exception.getMessage()); // Check the exact error message
        verify(itemRepo, times(1)).findByItemAvailableTrue();
    }

    @Test
    void testSearchProducts_Success() throws BackendErrorException {
        when(itemRepo.findByItemNameContainingIgnoreCaseAndItemAvailableTrue("Item")).thenReturn(List.of(item1));

        List<Item> result = itemService.searchProducts("Item");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isItemAvailable());
        verify(itemRepo, times(1)).findByItemNameContainingIgnoreCaseAndItemAvailableTrue("Item");
    }

    @Test
    void testSearchProducts_NotFound() {
        when(itemRepo.findByItemNameContainingIgnoreCaseAndItemAvailableTrue("NonExistent")).thenReturn(List.of());

        BackendErrorException exception = assertThrows(BackendErrorException.class, () -> {
            itemService.searchProducts("NonExistent");
        });

        assertEquals("The item you requested could not be found",exception.getMessage());
        verify(itemRepo, times(1)).findByItemNameContainingIgnoreCaseAndItemAvailableTrue("NonExistent");
    }

    @Test
    void testDeleteItem_Success() {
        when(itemRepo.findById("1")).thenReturn(Optional.of(item1));
        doNothing().when(itemRepo).delete(item1);

        String result = itemService.deleteItem("1");

        assertEquals("Item successfully deleted", result);
        verify(itemRepo, times(1)).findById("1");
        verify(itemRepo, times(1)).delete(item1);
    }

    @Test
    void testDeleteItem_NotFound() {
        when(itemRepo.findById("99")).thenReturn(Optional.empty());

        String result = itemService.deleteItem("99");

        assertEquals("Failed to delete item: Item not found", result);
        verify(itemRepo, times(1)).findById("99");
    }


}
