package com.trailiva.web.payload.response;

import lombok.Data;
import java.util.List;

@Data
public class UserProfile {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String imageUrl;
}
