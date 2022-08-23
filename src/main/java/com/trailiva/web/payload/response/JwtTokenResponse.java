package com.trailiva.web.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class JwtTokenResponse {
    @NotBlank(message = "Token cannot be null")
    private String jwtToken;

    @NotBlank(message = "Refresh token cannot be null")
    private String refreshToken;

    @Email(message = "Email must be valid")
    private String email;
}
