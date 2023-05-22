package com.firstproject.springredditclone.controller;

import com.firstproject.springredditclone.dto.AuthenticationResponse;
import com.firstproject.springredditclone.dto.LoginRequest;
import com.firstproject.springredditclone.dto.RefreshTokenRequest;
import com.firstproject.springredditclone.dto.RegisterRequest;
import com.firstproject.springredditclone.exceptions.SpringRedditException;
import com.firstproject.springredditclone.service.AuthService;
import com.firstproject.springredditclone.service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody RegisterRequest registerRequest)
    {
        try
        {
         authService.signup(registerRequest);
            return new ResponseEntity<>("User Registration Successful",
                    HttpStatus.OK);
        }catch (SpringRedditException exception)
        {
            return new ResponseEntity<>("User Registration Failed. Cause:"+exception.getMessage(),
                    HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("accountVerification/{token}")
    public ResponseEntity<String> verifyAccount(@PathVariable String token) {
        authService.verifyAccount(token);
        return new ResponseEntity<>("Account Activated Successfully", HttpStatus.OK);
    }

    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody LoginRequest loginRequest) {
        log.info(loginRequest.toString());
        return authService.login(loginRequest);
    }

    @PostMapping("/refresh/token")
    public AuthenticationResponse refreshTokens(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        return authService.refreshToken(refreshTokenRequest);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.deleteRefreshToken(refreshTokenRequest.getRefreshToken());
        return ResponseEntity.status(HttpStatus.OK).body("Refresh Token Deleted Successfully!!");
    }

}
