package com.chatbot.chatbot.utils;
import com.chatbot.chatbot.enums.BotRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Developed by ChhornSeyha
 * Date: 29/04/2026
 */

@Component
@Slf4j
public class PromptTemplateEngine {

    public String buildPrompt(BotRole role, String toneOverride, String language, String customPrompt) {
        // Start with the base template from the selected role

        String prompt = role.getPromptTemplate();

        // If role is CUSTOM, substitute the placeholder with the provided prompt
        if (role == BotRole.CUSTOM) {
            if (!StringUtils.hasText(customPrompt)) {
                throw new IllegalArgumentException(
                        "A custom prompt must be provided when using the CUSTOM role."
                );
            }
            prompt = customPrompt;
        }

        // Append tone override if provided
        // Example: "very formal" → adds "Always use a very formal tone."

        if (StringUtils.hasText(toneOverride)) {
            prompt += "\nTone instruction: Always use a " + toneOverride.trim() + " tone.";
        }

        // Append language instruction if provided
        // Example: "Reply in French" gets added at the end
        if (StringUtils.hasText(language)) {
            prompt += "\nLanguage instruction: " + language.trim();
        }
        log.debug("Built system prompt for role [{}] ({} chars)", role.name(), prompt.length());

        return prompt;

    }

    /**
     * Simplified version — just the role, no customizations.
     * Used when no tone or language overrides are needed.
     */

    public String build(BotRole role) {
        return buildPrompt(role, null, null, null);
    }


}
