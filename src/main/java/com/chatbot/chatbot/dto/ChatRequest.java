package com.chatbot.chatbot.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;



/**
 * Developed by ChhornSeyha
 * Date: 26/04/2026
 */


@Setter
@Getter
public class ChatRequest {
    @NotBlank(message = "sessionId cannot be blank")
    private String sessionId;

    @NotBlank(message = "message can't be blank")
    @Size(max = 2000, message = "message must not exceed 2000 character")
    private String message;
}
