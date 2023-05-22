package com.firstproject.springredditclone.controller;

import com.firstproject.springredditclone.dto.RegisterRequest;
import com.firstproject.springredditclone.exceptions.SpringRedditException;
import com.firstproject.springredditclone.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;

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

}
