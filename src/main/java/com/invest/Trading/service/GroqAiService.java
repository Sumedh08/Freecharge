package com.invest.Trading.service;

import java.util.List;
import java.util.Map;

public interface GroqAiService {
    String getChatResponse(List<Map<String, String>> messages, String userProfileContext);
}
