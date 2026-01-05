package com.invest.Trading.repository;

import com.invest.Trading.model.PortfolioSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PortfolioSnapshotRepository extends JpaRepository<PortfolioSnapshot, Long> {
    List<PortfolioSnapshot> findByUserIdAndTimestampAfter(Long userId, LocalDateTime timestamp);

    List<PortfolioSnapshot> findByUserId(Long userId);
}
