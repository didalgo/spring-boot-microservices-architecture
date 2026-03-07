package com.idalgo.daniel.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Login request with username and password")
public class LoginRequest {

    @Schema(
            description = "Username",
            example = "john.doe",
            required = true
    )
    @NotBlank(message = "Username is required")
    private String username;

    @Schema(
            description = "Password",
            example = "password123",
            required = true
    )
    @NotBlank(message = "Password is required")
    private String password;
}