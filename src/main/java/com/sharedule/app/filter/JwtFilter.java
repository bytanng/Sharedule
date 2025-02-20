package com.sharedule.app.filter;
import com.sharedule.app.service.user.JWTService;
import com.sharedule.app.service.user.MyUserDetailsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.lang.NonNull;

@Component
public class JwtFilter extends OncePerRequestFilter{
    @Autowired
    private JWTService jwtService;

    @Autowired
    ApplicationContext context;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request, 
            @NonNull HttpServletResponse response, 
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
                // Get "Bearer xxx" from clientside
                String authHeader = request.getHeader("Authorization");
                String token = null;
                String username = null;

                if(authHeader != null && authHeader.startsWith("Bearer ")){
                    token = authHeader.substring(7);

                    //check for username related to token
                    username = jwtService.extractUserName(token);
                }

                if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
                    //validate token
                    //check if token is valid and whether userdetails is in db
                    UserDetails userDetails = context.getBean(MyUserDetailsService.class).loadUserByUsername(username);

                    if(jwtService.validateToken(token, userDetails)){
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
                filterChain.doFilter(request, response);
    }
}