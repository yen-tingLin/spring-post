package com.example.post.config;

import com.example.post.exception.SpringPostException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;

    @Autowired
    SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        // csrf protetion mainly occurs when there are sessions or
        // we are using cookies to authenticate session information
        httpSecurity.csrf().disable()
                // allow all the incoming requests for backend api
                // whose end point start with /api/auth
                .authorizeRequests()
                .antMatchers("/api/auth/**")
                .permitAll()
                .anyRequest()
                .authenticated();

    }

    // algo : Bcrypt hashing.
    // as PasswordEncoder is an interface,
    // we have to manually create a bean inside configuretion class,
    // whenever we are autowire this bean,
    // we will get an instance of type BCryptPasswordEncoder
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // create authentication manager by authentication manager builder
    // inject AuthenticationManagerBuilder object by method injection
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authenticationManagerBuilder) {
        // take input of type UserDetailsService
        // UserDetailsService is an interface
        try {
            authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        
        } catch (Exception e) {
            throw new SpringPostException("Failed to create authentication manager", e);
        }
    } 
    
}
