package com.ms.user.dto;

import java.util.UUID;

public record UserResponseDTO(
        UUID userId,
        String name,
        String email
) {
}

