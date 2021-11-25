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
public class LoginRequest {
    @Email(message = "Invalid email")
    @NotBlank(message="email cannot be blank")
    private String email;

    @Size(min = 6, max = 20, message = "Invalid password")
    @NotBlank(message="password cannot be blank")
    private String password;
}
