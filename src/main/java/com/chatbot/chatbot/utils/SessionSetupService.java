package com.chatbot.chatbot.utils;

import com.chatbot.chatbot.enums.BotRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Developed by ChhornSeyha
 * Date: 29/04/2026
 */

@Slf4j
@Component
public class SessionSetupService {
    // Stores: sessionId → the resolved system prompt for that session
    private final Map<String,String> sessionPrompt = new ConcurrentHashMap<>();
    // Stores: sessionId → the BotRole for reference/display
    private final Map<String, BotRole> sessionRoles = new ConcurrentHashMap<>();

    /**
     * Registers a role and its resolved system prompt for a session.
     * Called once when a new session starts.
     */

    public void configure (String sessionId, BotRole role, String resolvedPrompt){
        sessionPrompt.put(sessionId,resolvedPrompt);
        sessionRoles.put(sessionId,role);
        log.info("Session [{}] → configured with role: {}", sessionId, role.getDisplayName());
    }

    /**
     * Returns the BotRole for this session (useful for response metadata).
     */

    public BotRole getRole(String sessionId){
        return sessionRoles.getOrDefault(sessionId, BotRole.FRIENDLY_COMPANION);
    }

    /**
     * Checks whether this session has been configured with a role.
     */
    public boolean isConfigured(String sessionId){
        return sessionPrompt.containsKey(sessionId);
    }

    /**
     * Cleans up config when a session is cleared.
     */
    public void clearSession(String sessionId){
        sessionPrompt.remove(sessionId);
        sessionRoles.remove(sessionId);
    }

    public String getSystemPrompt(String sessionId) {
        return sessionPrompt.getOrDefault(sessionId, defaultPrompt());
    }

    /*
     * Safe default — used if someone sends a message without starting a session.
     * Acts like FRIENDLY_COMPANION so the bot is never completely un-instructed.
     */

    private String defaultPrompt() {
        return """
               You are a helpful, friendly assistant.
               Be concise, clear, and natural in your responses.
               """;
    }

}
