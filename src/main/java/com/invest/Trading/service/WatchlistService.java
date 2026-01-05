package com.invest.Trading.service;

import com.invest.Trading.model.Stock;
import com.invest.Trading.model.User;
import com.invest.Trading.model.Watchlist;

public interface WatchlistService {

    Watchlist findUserWatchlist(Long userId) throws Exception;

    Watchlist createWatchList(User user);

    Watchlist findById(Long id) throws Exception;

    Stock addItemToWatchlist(Stock stock, User user) throws Exception;
}
