package com.invest.Trading.service;

import com.invest.Trading.model.Stock;
import java.util.List;

public interface StockService {
    List<Stock> getMarketStocks();

    Stock getStockDetails(String symbol);

    List<Stock> searchStocks(String keyword);
}
