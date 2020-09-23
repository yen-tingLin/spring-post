package com.example.post.config;

import com.example.post.exception.SpringPostException;
import com.example.post.security.JwtAuthenticationFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.BeanIds;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    SecurityConfig(
        UserDetailsService userDetailsService, 
        JwtAuthenticationFilter jwtAuthenticationFilter) 
    {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        // csrf protetion mainly occurs when there are sessions or
        // we are using cookies to authenticate session information
        httpSecurity.cors().and()
                .csrf().disable()
                // allow all the incoming requests for backend api
                // whose end point start with /api/auth
                .authorizeRequests()
                .antMatchers("/api/**")
                .permitAll()
                // .antMatchers(HttpMethod.GET, "/api/post/")
                // .permitAll()
                // .antMatchers(HttpMethod.GET, "/api/post/**")
                // .permitAll()                
                .antMatchers("/v2/api-docs",
                "/configuration/ui",
                "/swagger-resources/**",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**")
                .permitAll()
                .anyRequest()
                .authenticated();
        
        // pass jwtAuthenticationFilter to spring security, followed by 
        // filter UsernamePasswordAuthenticationFilter, so
        // spring check with jwtAuthenticationFilter first, and then check 
        // with filter UsernamePasswordAuthenticationFilter
        httpSecurity.addFilterBefore(jwtAuthenticationFilter, 
                        UsernamePasswordAuthenticationFilter.class);

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
            authenticationManagerBuilder.userDetailsService(userDetailsService)
                                .passwordEncoder(passwordEncoder());
        
        } catch (Exception e) {
            throw new SpringPostException("Failed to create authentication manager", e);
        }
    } 
    
}
