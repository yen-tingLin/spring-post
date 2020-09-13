package com.example.post.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

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
    
}
