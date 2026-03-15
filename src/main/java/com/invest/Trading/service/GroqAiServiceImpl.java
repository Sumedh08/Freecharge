package com.invest.Trading.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GroqAiServiceImpl implements GroqAiService {

    @Value("${groq.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getChatResponse(List<Map<String, String>> messages, String userProfileContext) {
        String url = "https://api.groq.com/openai/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        List<Map<String, String>> groqMessages = new ArrayList<>();
        
        // --- 1. System Persona (Axis) ---
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        
        String systemPrompt = "You are Axis, a super casual, very friendly animated buddy floating inside the user's investing platform called FinMate. " +
        "You speak exactly like a supportive human friend. Be natural, independent, and truly conversational. " +
        "Keep your responses short — like 2 to 3 sentences max per message. Ask one simple question at a time. " +
        "Explain financial concepts using your own creative, simple analogies. Do NOT over-explain.\\n\\n" +
        "CRITICAL KNOWLEDGE BOUNDARIES: Our website ONLY has these Mutual Fund categories: Large Cap, Mid Cap, Small Cap, Gold Fund. " +
        "DO NOT EVER mention Sectoral Funds, Index Funds, or ELSS — our platform does not have them! " +
        "The website has 4 tabs: Dashboard, Stocks, Mutual Funds, Portfolio.\\n\\n" +
        "RISK & PERFORMANCE KNOWLEDGE (3Y CAGR): " +
        "Large Cap: Nippon India (14.2%), SBI Bluechip (13.8%), ICICI Pru (15.6%). " +
        "Mid Cap: Axis Midcap (17.5%), Kotak Emerging (18.5%), HDFC Mid-Cap (16.4%). " +
        "Small Cap: Nippon India (24.0%), Quant Small Cap (28.1%). " +
        "Gold: SBI Gold Fund (8.5%), HDFC Gold (8.2%).\n\n" +
        "AGENTIC POINTING: You can highlight sidebar tabs! Tags: [POINT_DASHBOARD], [POINT_STOCKS], [POINT_MUTUAL_FUNDS], [POINT_PORTFOLIO]. " +
        "ALWAYS put these at the VERY END of your message.\n" +
        "STATE AWARENESS: Check 'CURRENTLY VIEWING TAB' in Context. If user is already on that tab, do NOT point to it.\n\n" +
        "STRICT MUTUAL FUND QUERY LOGIC (Investment Analysis):\n" +
        "When a user asks about returns (e.g., 'How much will I earn if I invest 5000 in Axis Midcap'):\n" +
        "1. EXTRACT: P = Monthly amount (default to 5000 if not in prompt or profile). Y = Years (default to 3 years if not specified). L = Lump Sum (use if mentioned).\n" +
        "2. RETRIEVE: X = Exact 3Y CAGR from the list above for the target fund.\n" +
        "3. CALCULATE:\n" +
        "   - Invested = (P * 12 * Y) + L\n" +
        "   - r = X / 100 / 12\n" +
        "   - Future Value = P * [((1+r)^(Y*12) - 1)/r] * (1+r) + (L * (1+X/100)^Y)\n" +
        "   - Profit = Future Value - Invested\n" +
        "4. RESPOND TEXTUALLY: Use this template:\n" +
        "   '{Fund Name} \n 3 Year CAGR: {X}% \n\n Monthly Investment: ₹{P} \n Investment Duration: {Y} years \n\n Total Invested: ₹{Invested} \n Estimated Value: ₹{Future Value} \n Estimated Profit: ₹{Profit} \n\n (returns are based on historical 3Y CAGR)'\n" +
        "5. EMIT TAG: [SHOW_SIP_CALC:{\"monthly\":P,\"rate\":X,\"lumpSum\":L,\"years\":Y}] at the absolute end.\n\n" +
        "PHASE 1 — ANALYSIS (Onboarding): " +
        "Ask these 4 questions ONE AT A TIME in conversation: " +
        "Q1: What are your financial goals? (emergency fund, travel, retirement, wealth building, buying land, etc.) " +
        "Q2: How much can you invest each month? " +
        "Q3: Have you invested before? (never/occasionally/regularly) " +
        "Q4: How actively do you want to manage your investments? (automated/occasional/active) " +
        "After all 4 answers, emit [PROFILE:{\"goals\":[\"goal1\"],\"monthly\":10000,\"experience\":\"beginner\",\"time\":\"low\"}]\n\n" +
        "GUARDRAILS: You provide educational estimates only. Never guarantee returns.";
        
        if (userProfileContext != null && !userProfileContext.isEmpty()) {
            systemPrompt += " \\n\\nCurrent User Context: " + userProfileContext;
        }
        
        systemMessage.put("content", systemPrompt);
        groqMessages.add(systemMessage);
        
        // --- 2. Injection of the Conversation History ---
        groqMessages.addAll(messages);

        // --- 3. Payload configuration ---
        // Using Llama 3.1 8B strictly as requested for open source inference
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "llama-3.1-8b-instant"); 
        requestBody.put("messages", groqMessages);
        requestBody.put("max_tokens", 800);
        requestBody.put("temperature", 0.7);

        try {
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            
            JsonNode root = objectMapper.readTree(response.getBody());
            return root.path("choices").get(0).path("message").path("content").asText();
            
        } catch (Exception e) {
            e.printStackTrace();
            return "I'm having a little trouble connecting to my open-source brain right now. Can we try again in a moment?";
        }
    }
}
