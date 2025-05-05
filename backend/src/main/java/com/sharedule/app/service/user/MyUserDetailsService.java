package com.sharedule.app.service.user;
import org.springframework.beans.factory.annotation.Autowired;
import com.sharedule.app.model.user.UserPrincipal;
import com.sharedule.app.model.user.*;
import com.sharedule.app.repository.user.UserRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("DEBUG - Attempting to load users: " + username);
        Users users = repo.findByUsername(username);
        if(users == null){
            System.out.println("ERROR - Users not found with username: " + username);
            throw new UsernameNotFoundException("Users not found with username: " + username);
        }
        System.out.println("DEBUG - Users found: " + users.getUsername());
        System.out.println("DEBUG - Users password hash: " + users.getPassword());
        return new UserPrincipal(users);
    }
}