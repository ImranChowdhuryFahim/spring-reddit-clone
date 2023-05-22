package com.firstproject.springredditclone.service;

import com.firstproject.springredditclone.dto.RegisterRequest;
import com.firstproject.springredditclone.exceptions.SpringRedditException;
import com.firstproject.springredditclone.model.NotificationEmail;
import com.firstproject.springredditclone.model.User;
import com.firstproject.springredditclone.model.VerificationToken;
import com.firstproject.springredditclone.repository.UserRepository;
import com.firstproject.springredditclone.repository.VerificationTokenRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;

    @Transactional
    public void signup(RegisterRequest registerRequest)
    {
        if(userNameExist(registerRequest.getUsername())) throw new SpringRedditException("Username already exist");
        if(emailExist(registerRequest.getEmail())) throw new SpringRedditException("Email already exist");

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCreated(Instant.now());
        user.setEnabled(false);

        try {
            userRepository.save(user);
        }catch (Exception exception)
        {
            throw new SpringRedditException("Exception while signup",exception);
        }
        String token = generateVerificationToken(user);
        mailService.sendMail(new NotificationEmail("Please Activate your Account",
                user.getEmail(), "Thank you for signing up to Spring Reddit, " +
                "please click on the below url to activate your account : " +
                "http://localhost:8080/api/auth/accountVerification/" + token));


    }

    public boolean userNameExist(String username)
    {
        Optional user = userRepository.findByUsername(username);
        return user.isPresent();
    }

    public boolean emailExist(String email)
    {
        Optional user = userRepository.findByEmail(email);
        return  user.isPresent();
    }


    private String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);

        verificationTokenRepository.save(verificationToken);
        return token;
    }

    @Transactional
    private void fetchUserAndEnable(VerificationToken verificationToken) {
        String username = verificationToken.getUser().getUsername();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new SpringRedditException("User not found with name - " + username));
        user.setEnabled(true);
        userRepository.save(user);
    }


    public void verifyAccount(String token) {
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
        fetchUserAndEnable(verificationToken.orElseThrow(() -> new SpringRedditException("Invalid Token")));
    }


}
