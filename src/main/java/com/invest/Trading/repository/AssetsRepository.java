package com.invest.Trading.repository;

import com.invest.Trading.model.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import java.util.List;
import java.util.Optional;

public interface AssetsRepository extends JpaRepository<Asset, Long> {

   List<Asset> findByUserId(Long userId);

   Asset findByUserIdAndStockId(Long userId, String stockId);

   Asset findByIdAndUserId(Long assetId, Long userId);

   Asset findAssetByUserIdAndStockId(Long userId, String stockId) throws Exception;

}