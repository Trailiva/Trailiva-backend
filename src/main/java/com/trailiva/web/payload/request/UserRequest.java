package com.trailiva.web.payload.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class UserRequest {
    @NotBlank(message = "First name can not be blank")
    private String firstName;

    @NotBlank(message = "Last name can not be blank")
    private String lastName;

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email can not be blank")
    private String email;

    @Size(min = 6, max = 20, message = "Invalid password")
    @NotBlank(message = "Password can not be blank")
    private String password;
}
