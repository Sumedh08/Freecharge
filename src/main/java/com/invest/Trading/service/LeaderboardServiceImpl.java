package com.invest.Trading.service;

import com.invest.Trading.model.Asset;
import com.invest.Trading.model.PortfolioSnapshot;
import com.invest.Trading.model.User;
import com.invest.Trading.repository.AssetsRepository;
import com.invest.Trading.repository.PortfolioSnapshotRepository;
import com.invest.Trading.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LeaderboardServiceImpl implements LeaderboardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssetsRepository assetsRepository;

    @Autowired
    private PortfolioSnapshotRepository snapshotRepository;

    @Override
    public List<Map<String, Object>> getWeeklyLeaderboard() {
        return calculateLeaderboard(LocalDateTime.now().minusDays(7));
    }

    @Override
    public List<Map<String, Object>> getMonthlyLeaderboard() {
        return calculateLeaderboard(LocalDateTime.now().minusDays(30));
    }

    @Override
    public List<Map<String, Object>> getAllTimeLeaderboard() {
        // For all time, we compare against the initial 1 Crore
        return calculateAllTimeLeaderboard();
    }

    private List<Map<String, Object>> calculateLeaderboard(LocalDateTime startTime) {
        List<User> allUsers = userRepository.findAll();
        List<Map<String, Object>> rankings = new ArrayList<>();

        for (User user : allUsers) {
            // Get mock current value (real value calculation would be complex with live
            // prices)
            // For now, calculating based on Asset Cost + Cash
            double currentValue = calculateTotalPortfolioValue(user);

            // Get user's value at startTime
            // Since we just started, we might not have snapshots.
            // Fallback to initial 1 Crore if no snapshot found.
            List<PortfolioSnapshot> snapshots = snapshotRepository.findByUserIdAndTimestampAfter(user.getId(),
                    startTime);
            double startValue = snapshots.isEmpty() ? 10000000.0 : snapshots.get(0).getTotalValue();

            double percentageReturn = ((currentValue - startValue) / startValue) * 100;

            Map<String, Object> entry = new HashMap<>();
            entry.put("id", user.getId());
            entry.put("fullName", user.getFullName());
            entry.put("return", percentageReturn);
            entry.put("currentValue", currentValue);
            rankings.add(entry);
        }

        rankings.sort((a, b) -> Double.compare((Double) b.get("return"), (Double) a.get("return")));
        return rankings.stream().limit(10).collect(Collectors.toList());
    }

    private List<Map<String, Object>> calculateAllTimeLeaderboard() {
        List<User> allUsers = userRepository.findAll();
        List<Map<String, Object>> rankings = new ArrayList<>();

        for (User user : allUsers) {
            double currentValue = calculateTotalPortfolioValue(user);
            double startValue = 10000000.0; // Fixed initial balance

            double percentageReturn = ((currentValue - startValue) / startValue) * 100;

            Map<String, Object> entry = new HashMap<>();
            entry.put("id", user.getId());
            entry.put("fullName", user.getFullName());
            entry.put("return", percentageReturn);
            entry.put("currentValue", currentValue);
            rankings.add(entry);
        }

        rankings.sort((a, b) -> Double.compare((Double) b.get("return"), (Double) a.get("return")));
        return rankings.stream().limit(10).collect(Collectors.toList());
    }

    private double calculateTotalPortfolioValue(User user) {
        double cash = user.getBalance().doubleValue();
        List<Asset> assets = assetsRepository.findByUserId(user.getId());
        double assetsValue = 0;
        for (Asset asset : assets) {
            // Ideally fetch real time price, but using buy price for simplicity/speed in
            // loop
            // or we need to inject StockService to get real price.
            // Using asset.getStock().getCurrentPrice() assuming it's fetched (might be
            // stale if not refreshed)
            // The Stock entity in Asset might be detached.
            // For this impl, we will trust the Asset's stored stock price or fetch it if
            // needed.
            // In a real app we'd bulk fetch prices.
            assetsValue += asset.getQuantity() * asset.getStock().getCurrentPrice();
        }
        return cash + assetsValue;
    }

    @Scheduled(cron = "0 0 0 * * ?") // Daily midnight
    public void captureSnapshots() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            double value = calculateTotalPortfolioValue(user);
            PortfolioSnapshot snapshot = new PortfolioSnapshot();
            snapshot.setUserId(user.getId());
            snapshot.setTotalValue(value);
            snapshot.setTimestamp(LocalDateTime.now());
            snapshotRepository.save(snapshot);
        }
    }
}
