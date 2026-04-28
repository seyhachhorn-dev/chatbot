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
     * 1. Load session history from memory : Read the notebook (load history)
     * 2. Build full message list (system prompt + history + new message) : Write what the user just said
     * 3. Call AI with full context
     * 4. Store both user message and AI reply in memory
     * 5. Return AI reply
     */

    private final ChatClient chatClient;
    private final ConversationMemoryService memory;

    /*
     * This is the instruction card we always give the AI at the start.
     * It tells the AI who it is and how it should behave.
     * It is always sent first — before any conversation history.
     */

    private static final String SYSTEM_PROMPT = """
               You are a helpful, friendly assistant with memory of the current conversation.
               Use context from the conversation history to give coherent, personalized responses.
               If the user told you their name or any details earlier, remember and use them.
               Be concise and natural in your replies.
            """;


    /*
     * Main method — handles one full conversation turn.
     *
     * Flow:
     *   1. Save the user's message to memory
     *   2. Build the full message list (system + history + new message)
     *   3. Send everything to the AI
     *   4. Save the AI's reply to memory
     *   5. Return the AI's reply
     */

    public String chat(String sessionId, String userMessage) {
        log.info("Session [{}] → new message received.", sessionId);
        // Step 1 — Remember what the user just said
        memory.saveMessage(sessionId, Role.USER, userMessage);
        // Step 2 — Prepare everything to send to the AI
        List<Message> fullConversation = buildConversation(sessionId);
        // Step 3 — Ask the AI
        String aiReply = chatClient.prompt()
                .messages(fullConversation)
                .call()
                .content();

        // Step 4 — Remember what the AI just said
        memory.saveMessage(sessionId, Role.ASSISTANT, aiReply);
        log.info("Session [{}] → reply ready. History size: {}", sessionId, memory.getHistorySize(sessionId));
        return aiReply;
    }



    /*
     * Builds the full list of messages to send to the AI.
     *
     * The order matters — AI reads top to bottom:
     *
     *   [1]       SYSTEM MESSAGE   → "You are a helpful assistant..."
     *   [2..N]    HISTORY          → everything said so far (user + AI turns)
     *   [N+1]     NEW USER MESSAGE → the latest thing the user just typed
     *
     * Why include history?
     *   Without it, the AI has no memory — every message feels like the first.
     *   With it, the AI can say " 아까 말했잖아 네 이름은 Seyha ."
     */

    private List<Message> buildConversation(String sessionId) {
        List<Message> messages = new ArrayList<>();
        // Always start with the system instruction
        messages.add(new SystemMessage(SYSTEM_PROMPT));
        // Add everything from memory (this already includes the latest user message)

        for (ConversationMessage pastMessage : memory.getHistory(sessionId)) {
            if (pastMessage.getRole() == Role.USER) {
                messages.add(new UserMessage(pastMessage.getContent()));
            } else {
                messages.add(new AssistantMessage(pastMessage.getContent()));
            }
        }
        log.debug("Session [{}] → sending {} messages to AI.", sessionId, messages.size());
        return messages;

    }


}
