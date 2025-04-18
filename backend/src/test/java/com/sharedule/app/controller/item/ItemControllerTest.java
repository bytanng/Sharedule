package com.sharedule.app.controller.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sharedule.app.dto.CreateItemDTO;
import com.sharedule.app.dto.EditItemDTO;
import com.sharedule.app.model.item.Item;
import com.sharedule.app.model.user.AppUsers;
import com.sharedule.app.service.item.ItemService;
import com.sharedule.app.service.user.JWTService;
import com.sharedule.app.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ItemControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

    @Mock
    private JWTService jwtService;

    @InjectMocks
    private ItemController itemController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    private AppUsers user;
    private Item item;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();

        user = new AppUsers();
        user.setUsername("testuser");

        item = new Item();
        item.setId("item1");
        item.setUser(user);
    }

    @Test
    void testCreateItem() throws Exception {
        CreateItemDTO createItemDTO = new CreateItemDTO();
        createItemDTO.setItemName("TestItem1"); // At least 8 chars, includes letters
        createItemDTO.setItemDescription("This is a valid description with more than eight words.");
        createItemDTO.setItemPrice(99.99);
        createItemDTO.setItemStock(10);
        createItemDTO.setItemAvailable(true);
        createItemDTO.setItemImage("https://example.com/image.jpg");

        when(jwtService.isTokenExpired(anyString())).thenReturn(false);
        when(userService.getUser(anyString())).thenReturn(user);
        when(itemService.createItem(any(CreateItemDTO.class), any())).thenReturn(item);

        mockMvc.perform(post("/create-item")
                        .header("Authorization", "Bearer validToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createItemDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("item1")); // adjust if your mock item has a different id
    }



    @Test
    void testGetUserItems() throws Exception {
        when(jwtService.isTokenExpired(anyString())).thenReturn(false);
        when(userService.getUser(anyString())).thenReturn(user);
        when(itemService.getItemsByUser(any())).thenReturn(List.of(item));

        mockMvc.perform(get("/items")
                        .header("Authorization", "Bearer validToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("item1"));
    }

    @Test
    void testGetItem() throws Exception {
        when(jwtService.isTokenExpired(anyString())).thenReturn(false);
        when(userService.getUser(anyString())).thenReturn(user);
        when(itemService.getItem("item1")).thenReturn(item);

        mockMvc.perform(get("/item/item1")
                        .header("Authorization", "Bearer validToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("item1"));
    }

    @Test
    void testSearchItems() throws Exception {
        when(jwtService.isTokenExpired(anyString())).thenReturn(false);
        when(userService.getUser(anyString())).thenReturn(user);
        when(itemService.searchItems("query")).thenReturn(List.of(item));

        mockMvc.perform(get("/items/search")
                        .param("query", "query")
                        .header("Authorization", "Bearer validToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("item1"));
    }

    @Test
    void testGetProducts() throws Exception {
        when(itemService.getProducts()).thenReturn(List.of(item));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("item1"));
    }

    @Test
    void testGetProduct() throws Exception {
        when(itemService.getItem("item1")).thenReturn(item);

        mockMvc.perform(get("/product/item1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("item1"));
    }

    @Test
    void testSearchProducts() throws Exception {
        when(itemService.searchProducts("query")).thenReturn(List.of(item));

        mockMvc.perform(get("/products/search")
                        .param("query", "query"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("item1"));
    }

    @Test
    void testDeleteItem() throws Exception {
        when(jwtService.isTokenExpired(anyString())).thenReturn(false);
        when(userService.getUser(anyString())).thenReturn(user);
        when(itemService.getItem("item1")).thenReturn(item);
        when(itemService.deleteItem("item1")).thenReturn("Item successfully deleted");

        mockMvc.perform(delete("/item/item1/delete")
                        .header("Authorization", "Bearer validToken"))
                .andExpect(status().isOk())
                .andExpect(content().string("The item has been permanently deleted"));
    }

    @Test
    void testEditItem() throws Exception {
        EditItemDTO editItemDTO = new EditItemDTO();
        when(jwtService.isTokenExpired(anyString())).thenReturn(false);
        when(userService.getUser(anyString())).thenReturn(user);
        when(itemService.getItem("item1")).thenReturn(item);
        when(itemService.updateItem(any(Item.class), any(EditItemDTO.class))).thenReturn(item);

        mockMvc.perform(put("/item/item1")
                        .header("Authorization", "Bearer validToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editItemDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("item1"));
    }
}
