package com.invest.Trading.service;

import com.invest.Trading.model.Stock;
import com.invest.Trading.model.User;
import com.invest.Trading.model.Watchlist;
import com.invest.Trading.repository.WatchlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WatchlistServiceImpl implements WatchlistService {
    @Autowired
    private WatchlistRepository watchlistRepository;

    @Override
    public Watchlist findUserWatchlist(Long userId) throws Exception {
        Watchlist watchlist = watchlistRepository.findByUserId(userId);
        if (watchlist == null) {
            throw new Exception("watch not found");
        }
        return watchlist;
    }

    @Override
    public Watchlist createWatchList(User user) {
        Watchlist watchlist = new Watchlist();
        watchlist.setUser(user);
        return watchlistRepository.save(watchlist);
    }

    @Override
    public Watchlist findById(Long id) throws Exception {
        Optional<Watchlist> optionalWatchlist = watchlistRepository.findById(id);
        if (optionalWatchlist.isEmpty()) {
            throw new Exception("watch list not found");
        }
        return optionalWatchlist.get();
    }

    @Override
    public Stock addItemToWatchlist(Stock stock, User user) throws Exception {
        Watchlist watchlist = findUserWatchlist(user.getId());

        if (watchlist.getStocks().contains(stock)) {
            watchlist.getStocks().remove(stock);
        } else
            watchlist.getStocks().add(stock);
        watchlistRepository.save(watchlist);
        return stock;
    }
}
