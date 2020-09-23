package com.example.post.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import com.example.post.dto.AuthenticationResponse;
import com.example.post.dto.LoginRequest;
import com.example.post.dto.RefreshTokenRequest;
import com.example.post.dto.RegisterRequest;
import com.example.post.exception.SpringPostException;
import com.example.post.model.NotificationEmail;
import com.example.post.model.RefreshToken;
import com.example.post.model.User;
import com.example.post.model.VerificationToken;
import com.example.post.repository.UserRepository;
import com.example.post.repository.VerificationTokenRepository;
import com.example.post.security.JwtProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthServiceImp implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    // constructor injection
    @Autowired
    AuthServiceImp(UserRepository userRepository, PasswordEncoder passwordEncoder,
            VerificationTokenRepository verificationTokenRepository, MailService mailService,
            AuthenticationManager authenticationManager, JwtProvider jwtProvider,
            RefreshTokenService refreshTokenService) 
    {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenRepository = verificationTokenRepository;
        this.mailService = mailService;
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.refreshTokenService = refreshTokenService;
    }

    // Transactional : rollback when runtime exception occured
    @Transactional
    @Override
    public void register(RegisterRequest registerRequest) {
        // seve user's info into database
        User newUser = new User();
        newUser.setUserName(registerRequest.getUserName());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        newUser.setEmail(registerRequest.getEmail());
        newUser.setCreateDate(Instant.now());
        newUser.setValidated(false);

        userRepository.save(newUser);

        String token = generateVerificationToken(newUser);

        // set notification email and send it to user by mailService's API
        NotificationEmail notificationEmail = new NotificationEmail();
        notificationEmail.setSubject("Please activate your account");
        notificationEmail.setRecipient(newUser.getEmail());
        notificationEmail.setBody(
                "Thank you for signing up to SpringPost, " + "please click on the below url to activate your account : "
                        + "http://localhost:8092/api/auth/accountVerification/" + token);

        mailService.sendMail(notificationEmail);

    }

    // generate a random string and sned it by email for verification
    private String generateVerificationToken(User newUser) {
        String randStr = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();

        verificationToken.setUser(newUser);
        verificationToken.setToken(randStr);

        verificationTokenRepository.save(verificationToken);

        return randStr;
    }

    @Override
    public void verifyAccount(String token) {

        // Optional 是值的容器，只有兩種狀態，不是有值就是沒值
        Optional<VerificationToken> databaseToken = verificationTokenRepository.findByToken(token);
        databaseToken.orElseThrow(() -> new SpringPostException("Invalid token"));

        // ** MY CODE **
        // try {
        // String tokenFound = databaseToken.getToken();
        // if(tokenFound.equals(token)) {
        // log.info(databaseToken.getUser().getUserName() + " verified successfully");
        // }
        // } catch(RuntimeException e) {
        // log.error("Can not find token. Failed to verify user.");
        // throw new SpringPostException("Can not find token. Failed to verify user",
        // e);
        // }

        fetchUserAndValidate(databaseToken.get());
    }

    @Transactional
    private void fetchUserAndValidate(VerificationToken validToken) {

        String username = validToken.getUser().getUserName();

        Optional<User> optionalUserFound = userRepository.findByUserName(username);
        optionalUserFound.orElseThrow(() -> new SpringPostException("User not found with name " + username));

        // get value (User object) from optional container
        User userFound = optionalUserFound.get();
        // change validation info to true and update user
        userFound.setValidated(true);
        userRepository.save(userFound);

        log.info(userFound.getUserName() + " verified successfully");
        log.info("Is " + userFound.getUserName() + " set valid ? " + userFound.isValidated());

        // ** MY CODE **
        // User validUser = validToken.getUser();
        // validUser.setValidated(true);

        // log.info(validUser.getUserName() + " verified successfully");
        // log.info("Is " + validUser.getUserName() + " set valid ? " +
        // validUser.isValidated());
    }

    @Override
    public AuthenticationResponse login(LoginRequest loginRequest) {
        // create UsernamePasswordAuthenticationToken object, and
        // pass it to AuthenticationManager method which then call UserDetailsService.
        // This authenticationManager will find the cretential in the background,
        // if they are matching, authenticationManager return an object called
        // Authentication
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUserName(), loginRequest.getPassword()));

        // store the authentication object into the s.
        // If we want to check a user is logged in or not, we can just
        // look up the security context for the authentication object,
        // and if the authentication object is found, the user is logged in.
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String authenticatedToken = jwtProvider.generateToken(authentication);

        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setUserName(loginRequest.getUserName());
        authenticationResponse.setAuthenticationToken(authenticatedToken);
        authenticationResponse.setExpiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()));
        authenticationResponse.setRefreshToken(refreshTokenService.generateRefreshToken().getToken());

        log.info(loginRequest.getUserName() + " logged in");

        return authenticationResponse;
    }

    @Transactional(readOnly = true)
    @Override
    public User getCurrentUser() {
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        
        if(principal == null) {
            throw new SpringPostException("principal is null");
        }

        log.info("user name from sacurity context : " + principal.getUsername());

        Optional<User> userOptional = userRepository.findByUserName(principal.getUsername());
        userOptional.orElseThrow(
                () -> new UsernameNotFoundException("User not found with name " + principal.getUsername()));

        return userOptional.get();
    }

    @Override
    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {

        refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
        String token = jwtProvider.generateTokenWithUserName(refreshTokenRequest.getUserName());

        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setAuthenticationToken(token);
        authenticationResponse.setExpiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()));
        authenticationResponse.setRefreshToken(refreshTokenRequest.getRefreshToken());
        authenticationResponse.setUserName(refreshTokenRequest.getUserName());

        return authenticationResponse;
    }

    @Override
    public boolean isLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return !(authentication instanceof AnonymousAuthenticationToken)
                    && authentication.isAuthenticated();

    }
 
}
