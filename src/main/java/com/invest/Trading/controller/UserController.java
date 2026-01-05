package com.invest.Trading.controller;

import com.invest.Trading.exception.UserNotFoundException;
import com.invest.Trading.model.User;
import com.invest.Trading.service.UserService;

import jdk.jshell.spi.ExecutionControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/api/users/profile")
    public ResponseEntity<?> getUserProfileHandler(
            @RequestHeader("Authorization") String jwt) {
        try {
            User user = userService.findUserProfileByJwt(jwt);
            user.setPassword(null);
            return new ResponseEntity<>(user, HttpStatus.ACCEPTED);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/api/users/{userId}")
    public ResponseEntity<?> findUserById(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String jwt) {
        try {
            User user = userService.findUserById(userId);
            user.setPassword(null);
            return new ResponseEntity<>(user, HttpStatus.ACCEPTED);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/api/users/email/{email}")
    public ResponseEntity<?> findUserByEmail(
            @PathVariable String email,
            @RequestHeader("Authorization") String jwt) {
        try {
            User user = userService.findUserByEmail(email);
            user.setPassword(null);
            return new ResponseEntity<>(user, HttpStatus.ACCEPTED);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/api/users/portfolio/history")
    public ResponseEntity<?> getPortfolioHistory(@RequestHeader("Authorization") String jwt) {
        try {
            User user = userService.findUserProfileByJwt(jwt);
            return new ResponseEntity<>(userService.getPortfolioHistory(user.getId()), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}