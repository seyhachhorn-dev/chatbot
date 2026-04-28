package com.chatbot.chatbot.model;

import com.chatbot.chatbot.enums.Role;
import lombok.*;

/**
 * Developed by ChhornSeyha
 * Date: 26/04/2026
 */

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ConversationMessage {
    private Role role;
    private String content;
}
