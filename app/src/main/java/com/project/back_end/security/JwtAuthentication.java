package com.project.back_end.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;
/**
 * Custom Authentication implementation for JWT
 */
public class JwtAuthentication implements Authentication {
    
    private final String email;
    private final String role;
    private boolean authenticated = true;
    
    public JwtAuthentication(String email, String role) {
        this.email = email;
        this.role = role;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Return role with prefix ROLE_ (Spring Security convention)
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
    }
    
    @Override
    public Object getCredentials() {
        return null; // JWT does not need to store password
    }
    
    @Override
    public Object getDetails() {
        return null;
    }
    
    @Override
    public Object getPrincipal() {
        return email;
    }
    
    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }
    
    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }
    
    @Override
    public String getName() {
        return email;
    }
    
    public String getRole() {
        return role;
    }
}
