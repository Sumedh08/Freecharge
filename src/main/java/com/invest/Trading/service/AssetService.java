package com.invest.Trading.service;

import com.invest.Trading.model.Asset;
import com.invest.Trading.model.Stock;
import com.invest.Trading.model.User;

import java.util.List;

public interface AssetService {
    Asset createAsset(User user, Stock stock, double quantity);

    Asset getAssetById(Long assetId);

    Asset getAssetByUserAndId(Long userId, Long assetId);

    List<Asset> getUsersAssets(Long userId);

    Asset updateAsset(Long assetId, double quantity) throws Exception;

    Asset findAssetByUserIdAndStockId(Long userId, String stockId) throws Exception;

    void deleteAsset(Long assetId);

}
