package com.example.post.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import com.example.post.dto.RegisterRequest;
import com.example.post.exception.SpringPostException;
import com.example.post.model.NotificationEmail;
import com.example.post.model.User;
import com.example.post.model.VerificationToken;
import com.example.post.repository.UserRepository;
import com.example.post.repository.VerificationTokenRepository;

import org.springframework.beans.factory.annotation.Autowired;
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

    // constructor injection
    @Autowired
    AuthServiceImp(
        UserRepository userRepository, 
        PasswordEncoder passwordEncoder,
        VerificationTokenRepository verificationTokenRepository,
        MailService mailService) 
    {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenRepository = verificationTokenRepository;
        this.mailService = mailService;
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
        notificationEmail.setBody("Thank you for signing up to SpringPost, " +
                "please click on the below url to activate your account : " +
                "http://localhost:8092/api/auth/accountVerification/" + token);

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
        //     String tokenFound = databaseToken.getToken();
        //     if(tokenFound.equals(token)) {
        //         log.info(databaseToken.getUser().getUserName() + " verified successfully");
        //     }           
        // } catch(RuntimeException e) {
        //     log.error("Can not find token. Failed to verify user.");
        //     throw new SpringPostException("Can not find token. Failed to verify user", e);
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
        // log.info("Is " + validUser.getUserName() + " set valid ? " + validUser.isValidated());
    }

    
 
}
