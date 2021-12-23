package com.trailiva.service;

import com.trailiva.web.payload.request.EmailRequest;

public interface EmailService {
    void sendUserVerificationEmail(EmailRequest emailRequest);
}
