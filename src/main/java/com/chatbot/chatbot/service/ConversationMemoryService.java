package com.chatbot.chatbot.service;

import com.chatbot.chatbot.enums.Role;
import com.chatbot.chatbot.model.ConversationMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Developed by ChhornSeyha
 * Date: 26/04/2026
 */

@Service
@Slf4j
public class ConversationMemoryService {
    private final Map<String, List<ConversationMessage>> sessionHistories =
            new ConcurrentHashMap<>();

    // Max messages to retain per session (prevents unbounded token growth)
    @Value("${chatbot.memory.max-history}")
    private int maxHistorySize;


    public void addMessage(String sessionId, Role role, String content){

     // computeIfAbsent: creates a new list if session doesn't exist yet
        List<ConversationMessage> history = sessionHistories
                .computeIfAbsent(sessionId,id-> new ArrayList<>());
        history.add(new ConversationMessage(role, content));


        while (history.size() > maxHistorySize){
            history.remove(0); // remove oldest message
            if(!history.isEmpty()){
                history.remove(0); // remove its pair
            }
        }
        log.debug("Session [{}] history size: {}", sessionId, history.size());

    }
    /**
     * Returns a read-only view of the session's conversation history.
     * Returns empty list for unknown sessions (new conversations).
     */
    public List<ConversationMessage> getHistory (String sessionId){
        return Collections.unmodifiableList(
                sessionHistories.getOrDefault(sessionId, new ArrayList<>())
        );
    }

    /**
     * Clears all history for a session (e.g. user clicks "New Chat").
     */

    public void clearHistory(String sessionId){
        sessionHistories.remove(sessionId);
        log.info("Cleared session: {}", sessionId);
    }


    /**
     * Returns current history size for a session (useful for response metadata).
     */
    public int getHistorySize(String sessionId){
        return sessionHistories.getOrDefault(sessionId,List.of()).size();
    }

}
