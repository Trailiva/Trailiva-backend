package com.trailiva.web.payload.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserResponse {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate datePublished;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate updatedDated;
}
