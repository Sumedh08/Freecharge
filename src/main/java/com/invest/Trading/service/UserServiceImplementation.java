package com.invest.Trading.service;

import com.invest.Trading.Domain.VERIFICATION_TYPE;
import com.invest.Trading.config.JwtProvider;
import com.invest.Trading.exception.UserNotFoundException;
import com.invest.Trading.model.User;
import com.invest.Trading.model.PortfolioSnapshot;
import com.invest.Trading.repository.PortfolioSnapshotRepository;
import com.invest.Trading.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import java.util.Optional;

import java.util.Optional;

@Service
public class UserServiceImplementation implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @Override
    public User findUserProfileByJwt(String jwt) {
        String email = jwtProvider.getEmailFromJwtToken(jwt);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Override
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Autowired
    private PortfolioSnapshotRepository snapshotRepository;

    @Override
    public User verifyUser(User user) {
        user.setVerified(true);
        return userRepository.save(user);
    }

    @Override
    public List<PortfolioSnapshot> getPortfolioHistory(Long userId) {
        return snapshotRepository.findByUserId(userId);
    }
}