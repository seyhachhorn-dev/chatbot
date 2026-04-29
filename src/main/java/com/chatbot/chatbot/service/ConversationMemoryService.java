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
    /*
     * ConcurrentHashMap keeps it safe when multiple users chat at the same time.
     */
    private final Map<String, List<ConversationMessage>> notebooks =
            new ConcurrentHashMap<>();

    /*
     * How many messages to keep per user.
     * Old messages are removed when this limit is reached.
     * This prevents sending too much data to the AI (saves cost + speed).
     */
    @Value("${chatbot.memory.max-history}")
    private int maxHistorySize;


    /*
     * Write a new message into the user's notebook.
     * Role tells us who wrote it — the user or the AI assistant.
     */
    public void saveMessage(String sessionId, Role role, String content){
        List<ConversationMessage> notebook = getOrCreateIfExist(sessionId);
        notebook.add(new ConversationMessage(role, content));
        trimIfLong(notebook);
        log.debug("Session [{}] → saved {} message. Total: {}", sessionId, role, notebook.size());


    }

    /*
    * Sliding-window trim that preserves the first user+assistant exchange.
    *
    * Layout after trim:
    *   [0] first user message    (pinned)
    *   [1] first assistant reply  (pinned)
    *   [2..N] most recent messages (sliding window)
    */
    private void trimIfLong(List<ConversationMessage> notebook) {
        if (notebook.size() <= maxHistorySize) {
            return;
        }
        if (notebook.size() < 4) {
            while (notebook.size() > maxHistorySize) {
                notebook.remove(0);
            }
            return;
        }
        while (notebook.size() > maxHistorySize && notebook.size() > 3) {
            notebook.remove(2);
            if (notebook.size() > 2) {
                notebook.remove(2);
            }
        }
    }

    /*
     * Get an existing notebook or create a new one if this session is new.
     * key = sessionId
     * value = list of messages (the "notebook")
     */

    private List<ConversationMessage> getOrCreateIfExist(String sessionId){
        /*
        * get exist one by sessionId if not it's replace session in id and get new empty list
        */
        return notebooks.computeIfAbsent(sessionId, id -> new ArrayList<>());
    }


    /**
     * Read all messages from the user's notebook (read-only view).
     * Returns empty list for unknown sessions (new conversations).
     */
    public List<ConversationMessage> getHistory (String sessionId){
     List<ConversationMessage> notebook = notebooks.getOrDefault(sessionId, new ArrayList<>());
     return Collections.unmodifiableList(notebook);
    }


    /**
     * Clears all history for a session (e.g. user clicks "New Chat").
     */
    public void clearHistory(String sessionId){
        notebooks.remove(sessionId);
        log.info("Cleared session: {}", sessionId);
    }


    /**
     * Returns current history size for a session (useful for response metadata).
     */
    public int getHistorySize(String sessionId){
        return notebooks.getOrDefault(sessionId,List.of()).size();
    }

}
