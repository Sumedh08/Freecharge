package com.invest.Trading.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.invest.Trading.model.MutualFund;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class MutualFundServiceImpl implements MutualFundService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String MF_API_URL = "https://api.mfapi.in/mf";

    @Override
    public List<MutualFund> getTopMutualFunds() {
        List<MutualFund> funds = new ArrayList<>();
        
        // Large Cap
        funds.add(new MutualFund("120465", "Nippon India Large Cap Fund", "Large Cap", 74.32, 35.1, 14.2, 16.1, "Low"));
        funds.add(new MutualFund("118989", "SBI Bluechip Fund", "Large Cap", 82.11, 28.5, 13.8, 14.5, "Low"));
        funds.add(new MutualFund("119062", "ICICI Pru Bluechip Fund", "Large Cap", 95.40, 31.2, 15.6, 17.0, "Low"));
        funds.add(new MutualFund("118272", "HDFC Top 100 Fund", "Large Cap", 1520.12, 29.8, 16.4, 15.8, "Low"));
        funds.add(new MutualFund("120716", "UTI Mastershare Unit Scheme", "Large Cap", 145.60, 26.4, 13.5, 14.9, "Low"));
        funds.add(new MutualFund("147703", "Navi Nifty 50 Index Fund", "Large Cap", 14.50, 25.1, 14.1, 15.2, "Low"));
        funds.add(new MutualFund("119063", "Mirae Asset Large Cap", "Large Cap", 112.50, 32.5, 16.1, 18.2, "Low"));
        funds.add(new MutualFund("119064", "Axis Bluechip Fund", "Large Cap", 65.20, 24.5, 12.8, 14.1, "Low"));
        funds.add(new MutualFund("119065", "Kotak Bluechip Fund", "Large Cap", 450.10, 30.1, 15.2, 16.4, "Low"));
        funds.add(new MutualFund("119066", "DSP Top 100 Equity Fund", "Large Cap", 340.20, 27.5, 13.9, 14.8, "Low"));

        // Mid Cap
        funds.add(new MutualFund("118991", "Kotak Emerging Equity Fund", "Mid Cap", 102.45, 45.2, 18.5, 21.0, "Moderate"));
        funds.add(new MutualFund("118274", "HDFC Mid-Cap Opportunities", "Mid Cap", 152.12, 48.5, 16.4, 20.5, "Moderate"));
        funds.add(new MutualFund("118992", "SBI Magnum Midcap Fund", "Mid Cap", 210.30, 42.1, 19.2, 22.4, "Moderate"));
        funds.add(new MutualFund("118993", "Nippon India Growth Fund", "Mid Cap", 305.40, 50.4, 21.5, 24.1, "Moderate"));
        funds.add(new MutualFund("118994", "Axis Midcap Fund", "Mid Cap", 98.60, 38.5, 17.5, 19.8, "Moderate"));
        funds.add(new MutualFund("118995", "ICICI Pru MidCap Fund", "Mid Cap", 250.80, 44.5, 18.8, 20.9, "Moderate"));
        funds.add(new MutualFund("118996", "DSP Midcap Fund", "Mid Cap", 185.30, 41.2, 17.9, 21.2, "Moderate"));
        funds.add(new MutualFund("118997", "Motilal Oswal Midcap Fund", "Mid Cap", 80.50, 55.4, 23.1, 25.4, "Moderate"));
        funds.add(new MutualFund("118998", "Edelweiss Mid Cap Fund", "Mid Cap", 75.20, 46.8, 19.5, 22.1, "Moderate"));
        funds.add(new MutualFund("118999", "Tata Mid Cap Growth Fund", "Mid Cap", 320.40, 43.5, 18.1, 20.5, "Moderate"));

        // Small Cap
        funds.add(new MutualFund("120718", "Nippon India Small Cap Fund", "Small Cap", 145.60, 65.4, 24.0, 28.5, "High"));
        funds.add(new MutualFund("118275", "Quant Small Cap Fund", "Small Cap", 240.50, 72.5, 28.1, 35.4, "High"));
        funds.add(new MutualFund("120719", "SBI Small Cap Fund", "Small Cap", 180.20, 58.4, 22.5, 26.8, "High"));
        funds.add(new MutualFund("120720", "HDFC Small Cap Fund", "Small Cap", 130.40, 62.1, 23.8, 27.1, "High"));
        funds.add(new MutualFund("120721", "Axis Small Cap Fund", "Small Cap", 95.50, 55.2, 21.4, 25.2, "High"));
        funds.add(new MutualFund("120722", "Kotak Small Cap Fund", "Small Cap", 210.60, 60.5, 23.1, 28.0, "High"));
        funds.add(new MutualFund("120723", "ICICI Pru Smallcap Fund", "Small Cap", 85.30, 59.8, 22.8, 26.5, "High"));
        funds.add(new MutualFund("120724", "DSP Small Cap Fund", "Small Cap", 155.20, 57.5, 21.9, 25.8, "High"));
        funds.add(new MutualFund("120725", "Tata Small Cap Fund", "Small Cap", 45.80, 68.2, 25.4, 29.5, "High"));
        funds.add(new MutualFund("120726", "Franklin India Smaller Cos", "Small Cap", 175.40, 64.5, 24.5, 28.2, "High"));

        // Gold
        funds.add(new MutualFund("118276", "SBI Gold Fund", "Gold", 22.10, 12.5, 8.5, 10.2, "Low"));
        funds.add(new MutualFund("118277", "HDFC Gold Fund", "Gold", 18.60, 11.8, 8.2, 9.8, "Low"));
        funds.add(new MutualFund("118278", "Nippon India Gold Savings", "Gold", 24.50, 13.1, 8.8, 10.5, "Low"));
        funds.add(new MutualFund("118279", "Kotak Gold Fund", "Gold", 21.40, 12.2, 8.4, 10.1, "Low"));
        funds.add(new MutualFund("118280", "ICICI Pru Regular Gold", "Gold", 19.80, 12.0, 8.3, 10.0, "Low"));
        funds.add(new MutualFund("118281", "Axis Gold Fund", "Gold", 17.50, 11.5, 8.1, 9.5, "Low"));
        funds.add(new MutualFund("118282", "Quantum Gold Savings Fund", "Gold", 25.20, 13.5, 9.0, 11.0, "Low"));
        funds.add(new MutualFund("118283", "DSP World Gold Fund", "Gold", 16.80, 10.5, 7.5, 9.0, "Moderate"));
        funds.add(new MutualFund("118284", "Invesco India Gold Fund", "Gold", 15.90, 11.2, 8.0, 9.6, "Low"));
        funds.add(new MutualFund("118285", "Aditya Birla Sun Life Gold", "Gold", 20.10, 12.8, 8.6, 10.3, "Low"));

        return funds;
    }

    @Override
    public MutualFund getMutualFundDetails(String schemeCode) {
        try {
            // Fetch live details from the free MFAPI
            String result = restTemplate.getForObject(MF_API_URL + "/" + schemeCode, String.class);
            JsonNode root = objectMapper.readTree(result);
            JsonNode meta = root.path("meta");
            JsonNode data = root.path("data");
            
            MutualFund fund = new MutualFund();
            fund.setSchemeCode(meta.path("scheme_code").asText());
            fund.setSchemeName(meta.path("scheme_name").asText());
            fund.setCategory(meta.path("scheme_category").asText());
            
            // Get the latest NAV
            if (data.isArray() && data.size() > 0) {
                fund.setCurrentNav(data.get(0).path("nav").asDouble());
            } else {
                fund.setCurrentNav(0.0);
            }
            // Mock default risk level & CAGR since the API only returns NAV history
            fund.setRiskLevel("Moderate");
            fund.setCagr1Y(25.0);
            fund.setCagr3Y(12.0);
            fund.setCagr5Y(14.0);
            
            return fund;
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback mock
            return new MutualFund(schemeCode, "Unknown Fund", "Unknown", 100.0, 10.0, 10.0, 10.0, "Moderate");
        }
    }

    @Override
    public List<MutualFund> searchMutualFunds(String keyword) {
        try {
            // Search using the free MFAPI
            String result = restTemplate.getForObject(MF_API_URL + "/search?q=" + keyword, String.class);
            JsonNode root = objectMapper.readTree(result);
            
            List<MutualFund> results = new ArrayList<>();
            if (root.isArray()) {
                // Limit to top 15 results to prevent massive payloads
                int count = 0;
                for (JsonNode node : root) {
                    if (count >= 15) break;
                    
                    MutualFund mf = new MutualFund();
                    mf.setSchemeCode(node.path("schemeCode").asText());
                    // API returns schemeName
                    mf.setSchemeName(node.path("schemeName").asText());
                    mf.setCategory("Search Result");
                    mf.setCurrentNav(0); // Cannot get NAV directly from search endpoint
                    mf.setRiskLevel("Unknown");
                    mf.setCagr1Y(0.0);
                    mf.setCagr3Y(0.0);
                    mf.setCagr5Y(0.0);
                    
                    results.add(mf);
                    count++;
                }
            }
            return results;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
