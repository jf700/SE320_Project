package com.digitaltherapy.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CrisisRequest {

    private Long userId;
    private String severityLevel;
    private String message;
    private LocalDateTime timestamp;
}