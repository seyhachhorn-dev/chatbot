package com.chatbot.chatbot.controller;

import com.chatbot.chatbot.dto.ChatRequest;
import com.chatbot.chatbot.dto.ChatResponse;
import com.chatbot.chatbot.dto.SessionStartRequest;
import com.chatbot.chatbot.enums.BotRole;
import com.chatbot.chatbot.service.ChatBotService;
import com.chatbot.chatbot.service.ConversationMemoryService;
import com.chatbot.chatbot.utils.PromptTemplateEngine;
import com.chatbot.chatbot.utils.SessionSetupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private final PromptTemplateEngine promptEngine;
    private final SessionSetupService sessionSetupService;


    @GetMapping("/roles")
    public ResponseEntity<List<Map<String, String>>> getRole() {

        List<Map<String, String>> role = Arrays.stream(BotRole.values()).
                map(r -> Map.of(
                        "role", r.name(),
                        "displayName", r.getDisplayName()
                ))
                .toList();
        return ResponseEntity.ok(role);

    }

    /**
     * Start a new session and configure its AI role.
     * Call this once before sending any messages.
     * <p>
     * POST /api/chat/session/start
     */

    @PostMapping("/session/start")
    public ResponseEntity<Map<String, String>> startSession(@Valid @RequestBody SessionStartRequest request) {

        // Build the final system prompt for this role + options

        String systemPrompt = promptEngine.buildPrompt(
                request.getRole(),
                request.getToneOverride(),
                request.getLanguage(),
                request.getCustomPrompt()
        );
        // Store the prompt against this session
        sessionSetupService.configure(request.getSessionId(),request.getRole(),systemPrompt);
        log.info("Session [{}] started with role: {}", request.getSessionId(),
                request.getRole().getDisplayName());

        return ResponseEntity.ok(Map.of(
                "sessionId", request.getSessionId(),
                "role", request.getRole().getDisplayName(),
                "status", "Session configured successfully"
        ));



    }


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
        sessionSetupService.clearSession(sessionId);
        return ResponseEntity.noContent().build();
    }
}
