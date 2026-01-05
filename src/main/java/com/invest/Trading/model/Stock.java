package com.invest.Trading.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stock {
    @Id
    private String id;
    private String symbol;
    private String name;
    private double currentPrice;
    private double priceChange;
    private double priceChangePercent;
    private long volume;
    private long marketCap;
}
