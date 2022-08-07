package com.trailiva.web.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePasswordRequest {
    @NotBlank(message = "old password cannot be blank")
    private String oldPassword;
    @NotBlank(message = "Password cannot be blank")
    private String password;
}
