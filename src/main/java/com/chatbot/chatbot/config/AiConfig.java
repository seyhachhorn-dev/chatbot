package com.chatbot.chatbot.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Developed by ChhornSeyha
 * Date: 26/04/2026
 */

@Configuration
public class AiConfig {
    @Bean
    public ChatClient chatClient(OpenAiChatModel openAiChatModel){
        return ChatClient.create(openAiChatModel);
    }
}
