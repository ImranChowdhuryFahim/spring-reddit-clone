package com.firstproject.springredditclone.service;

import com.firstproject.springredditclone.dto.AuthenticationResponse;
import com.firstproject.springredditclone.dto.LoginRequest;
import com.firstproject.springredditclone.dto.RefreshTokenRequest;
import com.firstproject.springredditclone.dto.RegisterRequest;
import com.firstproject.springredditclone.exceptions.SpringRedditException;
import com.firstproject.springredditclone.model.NotificationEmail;
import com.firstproject.springredditclone.model.User;
import com.firstproject.springredditclone.model.VerificationToken;
import com.firstproject.springredditclone.repository.UserRepository;
import com.firstproject.springredditclone.repository.VerificationTokenRepository;
import com.firstproject.springredditclone.security.JwtProvider;
import lombok.AllArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;


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

        userRepository.save(user);
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

   @Transactional(readOnly = true)
    public User getCurrentUser() {
        Jwt principal = (Jwt) SecurityContextHolder.
                getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(principal.getSubject())
                .orElseThrow(() -> new UsernameNotFoundException("User name not found - " + principal.getSubject()));
    }


    private String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);

        verificationTokenRepository.save(verificationToken);
        return token;
    }


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

    public AuthenticationResponse login(LoginRequest loginRequest) {

            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                    loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authenticate);
            String token = jwtProvider.generateToken(authenticate);
            return AuthenticationResponse.builder()
                    .authenticationToken(token)
                    .refreshToken(refreshTokenService.generateRefreshToken().getToken())
                    .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
                    .username(loginRequest.getUsername())
                    .build();
    }

    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
        String token = jwtProvider.generateTokenWithUserName(refreshTokenRequest.getUsername());
        return AuthenticationResponse.builder()
                .authenticationToken(token)
                .refreshToken(refreshTokenRequest.getRefreshToken())
                .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
                .username(refreshTokenRequest.getUsername())
                .build();
    }

    public boolean isLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated();
    }


}
