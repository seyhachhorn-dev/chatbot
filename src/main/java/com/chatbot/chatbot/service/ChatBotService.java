package com.chatbot.chatbot.service;

import com.chatbot.chatbot.enums.Role;
import com.chatbot.chatbot.model.ConversationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Developed by ChhornSeyha
 * Date: 26/04/2026
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatBotService {


    /**
     * Orchestrates conversation flow:
     * 1. Load session history from memory
     * 2. Build full message list (system prompt + history + new message)
     * 3. Call AI with full context
     * 4. Store both user message and AI reply in memory
     * 5. Return AI reply
     */

    private final ChatClient chatClient;
    private final ConversationMemoryService conversationMemoryService;

    // System prompt — defines the AI's persona and memory-aware behaviour
    // Injected here so it's sent on every request as the conversation anchor
    private static final String SYSTEM_PROMPT = """
               You are a helpful, friendly assistant with memory of the current conversation.
               Use context from the conversation history to give coherent, personalized responses.
               If the user told you their name or any details earlier, remember and use them.
                Be concise and natural in your replies.
            """;

//    public String chat(String msg){
//        log.info("sending message to AI model: {}", msg);
//
//        String aiReply = chatClient.prompt()
//                .user(msg)
//                .call()
//                .content();
//
//        log.info("Received reply from AI model");
//        return aiReply;
//
//    }

    /**
     * Processes a user message with full conversation context.
     *
     * @param sessionId unique session identifier
     * @param userMessage the new message from the user
     * @return AI-generated reply
     */
    public String chat(String sessionId, String userMessage){
        log.info("Session [{}] - Processing message", sessionId);
        // Step 1: Build message list = system prompt + history + new user message
        List<Message> messages = buildMessage(sessionId,userMessage);
        // Step 2: Store the user's message BEFORE calling AI
        conversationMemoryService.addMessage(sessionId,Role.USER, userMessage);
        String aiReply = chatClient.prompt()
                .messages(messages)
                .call()
                .content();
        // Step 4: Store AI's reply so future turns include it as context
    conversationMemoryService.addMessage(sessionId, Role.ASSISTANT, aiReply);
        log.info("Session [{}] - Reply generated. History size: {}",
                sessionId, conversationMemoryService.getHistorySize(sessionId));
        return aiReply;
    }


    private List<Message> buildMessage(String sessionId, String newUserMessage){
        List<Message> messages = new ArrayList<>();
        // 1. System prompt — anchors AI behaviour across all turns
        messages.add(new SystemMessage(SYSTEM_PROMPT));

        // 2. Conversation history — gives AI context of prior turns
        List<ConversationMessage> history = conversationMemoryService.getHistory(sessionId);

        for ( ConversationMessage msg : history){
            if(msg.getRole() == Role.USER){
                messages.add(new UserMessage(msg.getContent()));
            }else {
                messages.add(new AssistantMessage(msg.getContent()));
            }
        }
        // 3. New user message — the current turn (last in the list)

        messages.add(new UserMessage(newUserMessage));
        log.debug("Session [{}] - Sending {} messages to AI (1 system + {} history + 1 new)",
                sessionId, messages.size(), history.size());
      return messages;
    }
}
