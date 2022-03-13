package com.trailiva.web.payload.request;

import lombok.Data;

@Data
public class EmailRequest {
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String verificationCode;
}
