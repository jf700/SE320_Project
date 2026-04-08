package com.digitaltherapy.dto.request;

import jakarta.validation.constraints.*;

public final class SessionRequests {

    private SessionRequests() {}

    public record StartSessionRequest(
        @Min(1) @Max(10)
        Integer moodBefore
    ) {}

    public record ChatRequest(
        @NotBlank(message = "Message cannot be empty")
        @Size(max = 2000)
        String message
    ) {}

    public record EndSessionRequest(
        String reason,

        @Min(1) @Max(10)
        Integer moodAfter
    ) {}
}
