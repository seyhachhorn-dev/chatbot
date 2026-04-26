package com.chatbot.chatbot.controller;

import com.chatbot.chatbot.dto.ChatRequest;
import com.chatbot.chatbot.dto.ChatResponse;
import com.chatbot.chatbot.service.ChatBotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    /**
     * Handles a single-turn chat request.
     *
     * @Valid triggers Bean Validation on ChatRequest before the method body runs.
     * If validation fails, Spring returns a 400 Bad Request automatically.
     */
    @PostMapping
    public ResponseEntity<ChatResponse> chat (@Valid @RequestBody ChatRequest chatRequest){
        log.info("received chat request");
        String reply = chatBotService.chat(chatRequest.getMessage());
        return ResponseEntity.status(HttpStatus.CREATED).body(new ChatResponse(reply));
    }
}
