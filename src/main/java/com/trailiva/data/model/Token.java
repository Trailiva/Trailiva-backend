package com.trailiva.data.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document
public class Token {
    @Id
    private String id;
    private String token;
    private TokenType type;
    @DBRef
    private User user;
    private LocalDateTime expiry;
}
