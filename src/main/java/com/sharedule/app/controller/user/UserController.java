package com.sharedule.app.controller.user;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.sharedule.app.service.user.UserService;
import org.springframework.http.ResponseEntity;
import com.sharedule.app.dto.UserRegistrationDTO;
import java.util.List;

import com.sharedule.app.model.user.Users;

@RestController
public class UserController {
    
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegistrationDTO userRegistrationDTO){
        String response = userService.register(userRegistrationDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public String login(@RequestBody Users user){
        return userService.verify(user);
    }

    @GetMapping("/users")
    public List<Users> getAllUsers(){
        return userService.getAllUsers();
    }
}
