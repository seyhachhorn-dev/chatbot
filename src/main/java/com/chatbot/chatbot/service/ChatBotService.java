package com.chatbot.chatbot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Developed by ChhornSeyha
 * Date: 26/04/2026
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatBotService {
    private final ChatClient chatClient;

    public String chat(String msg){
        log.info("sending message to AI model: {}", msg);

        String aiReply = chatClient.prompt()
                .user(msg)
                .call()
                .content();

        log.info("Received reply from AI model");
        return aiReply;

    }
}
