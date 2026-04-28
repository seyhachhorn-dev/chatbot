package com.chatbot.chatbot.controller;

import com.chatbot.chatbot.dto.ChatRequest;
import com.chatbot.chatbot.dto.ChatResponse;
import com.chatbot.chatbot.service.ChatBotService;
import com.chatbot.chatbot.service.ConversationMemoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Developed by ChhornSeyha
 * Date: 26/04/2026
 */

@RestController
@RequestMapping("api/v1/chatbot")
@Slf4j
@RequiredArgsConstructor
public class ChatbotController {
    private final ChatBotService chatBotService;
    private final ConversationMemoryService conversationMemoryService;


    /**
     * Handles a single-turn chat request.
     *
     * @Valid triggers Bean Validation on ChatRequest before the method body runs.
     * If validation fails, Spring returns a 400 Bad Request automatically.
     */
    @PostMapping
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        log.info("Chat request received for session: {}", request.getSessionId());
        String reply = chatBotService.chat(request.getSessionId(), request.getMessage());

        return ResponseEntity.ok(ChatResponse.builder()
                .sessionId(request.getSessionId())
                .reply(reply)
                .historySize(conversationMemoryService.getHistorySize(request.getSessionId()))
                .build());
    }


    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> clearSession(@PathVariable String sessionId) {
        conversationMemoryService.clearHistory(sessionId);
        return ResponseEntity.noContent().build();
    }
}
