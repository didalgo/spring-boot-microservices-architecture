package com.idalgo.daniel.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Authentication response with tokens")
public class AuthResponse {

    @Schema(
            description = "JWT access token",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    private String accessToken;

    @Schema(
            description = "Refresh token for renewing access token",
            example = "550e8400-e29b-41d4-a716-446655440000"
    )
    private String refreshToken;

    @Schema(
            description = "Token type",
            example = "Bearer"
    )
    @Builder.Default
    private String tokenType = "Bearer";

    @Schema(
            description = "Access token expiration time in seconds",
            example = "3600"
    )
    private Long expiresIn;

    @Schema(
            description = "Username",
            example = "john.doe"
    )
    private String username;

    @Schema(
            description = "User email",
            example = "john.doe@example.com"
    )
    private String email;
}