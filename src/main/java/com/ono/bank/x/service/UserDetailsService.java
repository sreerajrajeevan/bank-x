package com.ono.bank.x.service;

import com.ono.bank.x.model.AppUser;
import com.ono.bank.x.model.UserRole;
import com.ono.bank.x.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Fetch user from database by username
        AppUser appUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Convert roles to GrantedAuthority (SimpleGrantedAuthority)
        Set<SimpleGrantedAuthority> authorities = appUser.getUserRoles().stream()  // Use getUserRoles() here
                .map(UserRole::getName)  // Get role name (like "ROLE_USER", "ROLE_ADMIN")
                .map(SimpleGrantedAuthority::new)  // Convert role name to SimpleGrantedAuthority
                .collect(Collectors.toSet());

        // Return the user details with authorities (roles)
        return org.springframework.security.core.userdetails.User
                .withUsername(appUser.getUsername())
                .password(appUser.getPassword())  // Ensure password is encoded
                .authorities(authorities)  // Set authorities (roles)
                .build();
    }
}
