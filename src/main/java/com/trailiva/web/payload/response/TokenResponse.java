package com.trailiva.web.payload.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.trailiva.data.model.TokenType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TokenResponse {
        private String token;
        private TokenType type;
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        private LocalDateTime expiry;
}
