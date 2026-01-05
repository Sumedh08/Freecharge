package com.invest.Trading.controller;

import com.invest.Trading.service.LeaderboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {

    @Autowired
    private LeaderboardService leaderboardService;

    @GetMapping("/weekly")
    public ResponseEntity<List<Map<String, Object>>> getWeeklyLeaderboard() {
        return new ResponseEntity<>(leaderboardService.getWeeklyLeaderboard(), HttpStatus.OK);
    }

    @GetMapping("/monthly")
    public ResponseEntity<List<Map<String, Object>>> getMonthlyLeaderboard() {
        return new ResponseEntity<>(leaderboardService.getMonthlyLeaderboard(), HttpStatus.OK);
    }

    @GetMapping("/all-time")
    public ResponseEntity<List<Map<String, Object>>> getAllTimeLeaderboard() {
        return new ResponseEntity<>(leaderboardService.getAllTimeLeaderboard(), HttpStatus.OK);
    }
}
