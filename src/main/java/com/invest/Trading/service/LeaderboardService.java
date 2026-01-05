package com.invest.Trading.service;

import com.invest.Trading.model.User;

import java.util.List;
import java.util.Map;

public interface LeaderboardService {
    List<Map<String, Object>> getWeeklyLeaderboard();

    List<Map<String, Object>> getMonthlyLeaderboard();

    List<Map<String, Object>> getAllTimeLeaderboard();

    void captureSnapshots(); // Scheduled task to capture daily snapshots
}
