package com.idalgo.daniel.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to refresh access token")
public class RefreshTokenRequest {

    @Schema(
            description = "Refresh token",
            example = "550e8400-e29b-41d4-a716-446655440000",
            required = true
    )
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}