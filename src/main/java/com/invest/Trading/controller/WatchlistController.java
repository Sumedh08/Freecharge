package com.invest.Trading.controller;

import com.invest.Trading.model.Stock;
import com.invest.Trading.model.User;
import com.invest.Trading.model.Watchlist;
import com.invest.Trading.service.StockService;
import com.invest.Trading.service.UserService;
import com.invest.Trading.service.WatchlistService;
import jdk.jshell.spi.ExecutionControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/watchlist")
public class WatchlistController {
        private final WatchlistService watchlistService;
        private final UserService userService;

        @Autowired
        private StockService stockService;

        @Autowired
        public WatchlistController(WatchlistService watchlistService,
                        UserService userService) {
                this.watchlistService = watchlistService;
                this.userService = userService;
        }

        @GetMapping("/user")
        public ResponseEntity<Watchlist> getUserWatchlist(
                        @RequestHeader("Authorization") String jwt) throws Exception {

                User user = userService.findUserProfileByJwt(jwt);
                Watchlist watchlist = watchlistService.findUserWatchlist(user.getId());
                return ResponseEntity.ok(watchlist);

        }

        @PostMapping("/create")
        public ResponseEntity<Watchlist> createWatchlist(
                        @RequestHeader("Authorization") String jwt) throws ExecutionControl.UserException {
                User user = userService.findUserProfileByJwt(jwt);
                Watchlist createdWatchlist = watchlistService.createWatchList(user);
                return ResponseEntity.status(HttpStatus.CREATED).body(createdWatchlist);
        }

        @GetMapping("/{watchlistId}")
        public ResponseEntity<Watchlist> getWatchlistById(
                        @PathVariable Long watchlistId) throws Exception {

                Watchlist watchlist = watchlistService.findById(watchlistId);
                return ResponseEntity.ok(watchlist);

        }

        @PatchMapping("/add/stock/{stockId}")
        public ResponseEntity<Stock> addItemToWatchlist(
                        @RequestHeader("Authorization") String jwt,
                        @PathVariable String stockId) throws Exception {

                User user = userService.findUserProfileByJwt(jwt);
                Stock stock = stockService.getStockDetails(stockId);
                Stock addedStock = watchlistService.addItemToWatchlist(stock, user);
                return ResponseEntity.ok(addedStock);

        }
}
