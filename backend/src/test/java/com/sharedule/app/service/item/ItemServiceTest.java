package com.sharedule.app.service.item;


import com.sharedule.app.dto.CreateItemDTO;
import com.sharedule.app.dto.EditItemDTO;
import com.sharedule.app.exception.BackendErrorException;
import com.sharedule.app.exception.ExistsInRepoException;
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


    


}
