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

        appUser = new AppUsers();
        appUser.setUsername("testUser");
        appUser.setEmail("test@hotmail.com.com");
        appUser.setPassword("hashedPassword");
        appUser.setRole("USER");
    }

    @Test
    void createItem_Success() {

        Item savedItem = new Item();
        savedItem.setItemName(createItemDTO.getItemName());
        savedItem.setItemDescription(createItemDTO.getItemDescription());
        savedItem.setItemPrice(createItemDTO.getItemPrice());
        savedItem.setItemStock(createItemDTO.getItemStock());
        savedItem.setItemAvailable(createItemDTO.isItemAvailable());
        savedItem.setItemImage(createItemDTO.getItemImage());
        savedItem.setUser(appUser);

        when(itemRepo.save(any(Item.class))).thenReturn(savedItem);

        Item result = itemService.createItem(createItemDTO, appUser);

        assertNotNull(result);
        assertEquals(createItemDTO.getItemName(), result.getItemName());
        assertEquals(createItemDTO.getItemDescription(), result.getItemDescription());
        assertEquals(createItemDTO.getItemPrice(), result.getItemPrice());
        assertEquals(createItemDTO.getItemStock(), result.getItemStock());
        assertEquals(createItemDTO.isItemAvailable(), result.isItemAvailable());
        assertEquals(createItemDTO.getItemImage(), result.getItemImage());
        assertEquals(appUser, result.getUser());

        verify(itemRepo, times(1)).save(any(Item.class));
    }

    @Test
    void createItem_Success() {

    }

}
