package com.idalgo.daniel.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User registration request")
public class RegisterRequest {

    @Schema(
            description = "Username (3-50 characters)",
            example = "john.doe",
            required = true
    )
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Schema(
            description = "Email address",
            example = "john.doe@example.com",
            required = true
    )
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @Schema(
            description = "Password (6-100 characters)",
            example = "password123",
            required = true
    )
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;

    @Schema(
            description = "First name",
            example = "John"
    )
    private String firstName;

    @Schema(
            description = "Last name",
            example = "Doe"
    )
    private String lastName;
}