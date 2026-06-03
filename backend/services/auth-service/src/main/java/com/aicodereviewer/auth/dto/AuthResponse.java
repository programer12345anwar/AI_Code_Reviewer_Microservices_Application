package com.aicodereviewer.auth.dto;

import com.aicodereviewer.common.enums.Role;

public record AuthResponse(
    String token,
    Long userId,
    String email,
    Role role
) {
}
