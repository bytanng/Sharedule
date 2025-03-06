package com.sharedule.app.service.user;
import org.springframework.beans.factory.annotation.Autowired;
import com.sharedule.app.model.user.UserPrincipal;
import com.sharedule.app.model.user.Users;
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
        System.out.println("DEBUG - Attempting to load user: " + username);
        Users user = repo.findByUsername(username);
        if(user == null){
            System.out.println("ERROR - User not found with username: " + username);
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        System.out.println("DEBUG - User found: " + user.getUsername());
        System.out.println("DEBUG - User password hash: " + user.getPassword());
        return new UserPrincipal(user);
    }
}