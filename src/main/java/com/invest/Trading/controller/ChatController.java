package com.invest.Trading.controller;

import com.invest.Trading.model.ChatRequest;
import com.invest.Trading.service.GroqAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private GroqAiService groqAiService;

    @PostMapping
    public ResponseEntity<Map<String, String>> chat(@RequestBody ChatRequest request) {
        // Securely pass the user's conversation to the Groq Open-Source backend
        String aiResponse = groqAiService.getChatResponse(request.getMessages(), request.getContext());
        Map<String, String> response = Map.of("reply", aiResponse);
        return ResponseEntity.ok(response);
    }
}
