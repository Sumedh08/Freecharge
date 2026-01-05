package com.invest.Trading.service;

import com.invest.Trading.Domain.VERIFICATION_TYPE;
import com.invest.Trading.exception.UserNotFoundException;
import com.invest.Trading.model.PortfolioSnapshot;
import com.invest.Trading.model.User;
import java.util.List;
import jdk.jshell.spi.ExecutionControl;

public interface UserService {

    public User findUserProfileByJwt(String jwt) throws UserNotFoundException;

    public User findUserByEmail(String email) throws UserNotFoundException;

    public User findUserById(Long userId) throws UserNotFoundException;

    public User verifyUser(User user) throws UserNotFoundException;

    public List<PortfolioSnapshot> getPortfolioHistory(Long userId);
}