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
public class MutualFund {
    @Id
    private String schemeCode;
    private String schemeName;
    private String category;
    private double currentNav;
    private double cagr1Y;
    private double cagr3Y;
    private double cagr5Y;
    private String riskLevel;
}
