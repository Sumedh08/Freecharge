package com.invest.Trading.controller;

import com.invest.Trading.model.Stock;
import com.invest.Trading.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stocks")
public class StockController {

    @Autowired
    private StockService stockService;

    @GetMapping
    public ResponseEntity<List<Stock>> getMarketStocks() {
        return new ResponseEntity<>(stockService.getMarketStocks(), HttpStatus.OK);
    }

    @GetMapping("/{symbol}")
    public ResponseEntity<Stock> getStockDetails(@PathVariable String symbol) {
        return new ResponseEntity<>(stockService.getStockDetails(symbol), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Stock>> searchStocks(@RequestParam String keyword) {
        return new ResponseEntity<>(stockService.searchStocks(keyword), HttpStatus.OK);
    }
}
