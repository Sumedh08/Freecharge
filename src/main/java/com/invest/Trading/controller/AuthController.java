package com.invest.Trading.controller;

import com.invest.Trading.exception.UserNotFoundException;
import com.invest.Trading.request.LoginRequest;
import com.invest.Trading.service.*;
import com.invest.Trading.config.JwtProvider;
import com.invest.Trading.model.User;
import com.invest.Trading.repository.UserRepository;
import com.invest.Trading.response.AuthResponse;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.math.BigDecimal;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(JwtProvider jwtProvider,
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder) {
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler(@RequestBody User user) {
        Optional<User> isEmailExist = userRepository.findByEmail(user.getEmail());

        if (isEmailExist.isPresent()) {
            throw new RuntimeException("Email is already used with another account");
        }

        User newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setFullName(user.getFullName());
        newUser.setMobile(user.getMobile());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        newUser.setBalance(new BigDecimal("10000000")); // 1 Crore INR Initial Balance

        userRepository.save(newUser);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage("Register Success");
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signing(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtProvider.generateToken(authentication);

            AuthResponse authResponse = new AuthResponse();
            authResponse.setMessage("Login Success");
            authResponse.setJwt(token);
            return new ResponseEntity<>(authResponse, HttpStatus.OK);
        } catch (BadCredentialsException e) {
            AuthResponse authResponse = new AuthResponse();
            authResponse.setMessage("Invalid credentials");
            return new ResponseEntity<>(authResponse, HttpStatus.UNAUTHORIZED);
        }
    }
}
