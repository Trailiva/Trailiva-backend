package com.trailiva.web.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordRequest {
    @NotBlank(message = "Token must be valid")
    private String token;

    @Size(min = 6, max = 20, message = "Password cannot be blank")
    private String password;

    @NotBlank(message = "password cannot be blank")
    private String oldPassword;
}
