package com.chatbot.chatbot.dto;

import com.chatbot.chatbot.enums.BotRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Developed by ChhornSeyha
 * Date: 29/04/2026
 */


/**
 * Request to start (or reconfigure) a session with a specific AI role.
 * <p>
 * Example JSON — picking a built-in role:
 * {
 *   "sessionId": "abc-123",
 *   "role": "ENGLISH_TEACHER",
 *   "toneOverride": "very encouraging",
 *   "language": "Reply in simple English"
 * }
 * <p>
 * Example JSON — using a custom prompt:
 * {
 *   "sessionId": "abc-123",
 *   "role": "CUSTOM",
 *   "customPrompt": "You are a Khmer history expert. Answer only about Cambodian history."
 * }
 */

@Setter
@Getter
public class SessionStartRequest {
    @NotBlank(message = "sessionId is required")
    private String sessionId;

    @NotNull(message = "role is required")
    private BotRole role;

    private String toneOverride;

    private String language;

    // Required only when role = CUSTOM
    private String customPrompt;
}
