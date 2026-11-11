package com.ms.email.dto;

import com.ms.email.enums.StatusEmail;

import java.time.LocalDateTime;
import java.util.UUID;

public record EmailResponseDTO(
        UUID emailId,
        UUID userId,
        String emailFrom,
        String emailTo,
        String subject,
        String text,
        LocalDateTime sendDateEmail,
        StatusEmail statusEmail
) {
}

