package com.chatbot.chatbot.dto;

import lombok.*;

/**
 * Developed by ChhornSeyha
 * Date: 26/04/2026
 */

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatResponse {
    private String sessionId;
    private String reply;
    private int historySize;
}
