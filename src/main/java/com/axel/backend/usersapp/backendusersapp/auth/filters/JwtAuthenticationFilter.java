package com.axel.backend.usersapp.backendusersapp.auth.filters;

import com.axel.backend.usersapp.backendusersapp.models.entities.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.*;

import static com.axel.backend.usersapp.backendusersapp.auth.TokenJwtConfig.*;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        User user = null;
        String username = null;
        String password = null;

        //Get body from request
        //ObjectMapper instance to serialize responses and deserialize requests
        try {
            user = new ObjectMapper().readValue(request.getInputStream(), User.class);
            username = user.getUsername();
            password = user.getPassword();

            /*logger.info("Username from request InputStream (body request)" + username);
            logger.info("Password from request InputStream (body request)" + password);*/

        } catch (IOException e){
            e.printStackTrace();
        }

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        return authenticationManager.authenticate(authToken);
    }
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
            String username = ((org.springframework.security.core.userdetails.User)authResult.getPrincipal())
                                .getUsername();
            Collection<? extends GrantedAuthority> roles = authResult.getAuthorities();
            boolean isAdmin = roles.stream().anyMatch(role-> role.getAuthority().equals("ROLE_ADMIN"));

            //Adding info(claims) to JWT
            Claims claims = Jwts.claims();
            claims.put("authorities", new ObjectMapper().writeValueAsString(roles));
            claims.put("isAdmin", isAdmin);
            claims.put("username", username);

            //Token generation
            String token = Jwts.builder()
                    .setClaims(claims)
                    .setSubject(username)
                    .signWith(SECRET_KEY)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                    .compact();

            response.addHeader(HEADER_AUTHORIZATION, PREFIX_TOKEN+ token);

            Map<String, Object> responseBody = new HashMap<>();

            responseBody.put("token", token);
            responseBody.put("message", String.format("%s, login successfully", username));
            responseBody.put("username", username);

            //HashMap to JSON with ObjectMapper
            response.getWriter().write(new ObjectMapper().writeValueAsString(responseBody));
            response.setStatus(200);
            response.setContentType("application/json");
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
            Map<String, Object> responseBody = new HashMap<>();

            responseBody.put("message", "Username or password incorrect");
            responseBody.put("error", failed.getMessage());

            //HashMap to JSON with ObjectMapper
            response.getWriter().write(new ObjectMapper().writeValueAsString(responseBody));
            response.setStatus(401);
            response.setContentType("application/json");
    }
}
