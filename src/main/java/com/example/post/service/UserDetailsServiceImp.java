package com.example.post.service;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import com.example.post.exception.SpringPostException;
import com.example.post.model.User;
import com.example.post.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class UserDetailsServiceImp implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    UserDetailsServiceImp(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // read user info from database, and
    // the returned UserDetails will return to authentication manager
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByUserName(username);
        User userFound = optionalUser.orElseThrow(() -> new SpringPostException("User not found with " + username));

        return new org.springframework.security.core.userdetails.User(
                userFound.getUserName(), userFound.getPassword(), userFound.isValidated(), 
                true, true, true, getAuthorities("USER"));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(String role) {
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    
    
}
