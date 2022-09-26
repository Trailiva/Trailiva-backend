package com.trailiva.web.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
public class JwtTokenResponse {
    @NotBlank(message = "Token cannot be null")
    private String jwtToken;

    @NotBlank(message = "Refresh token cannot be null")
    private String refreshToken;

    @Email(message = "Email must be valid")
    private String email;
}
