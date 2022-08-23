package com.trailiva.data.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.trailiva.data.model.TokenType.REFRESH;
import static com.trailiva.data.model.TokenType.REFRESH_TOKEN;

@Setter
@Getter
@Entity
@NoArgsConstructor
public class Token {
    private final static long EXPIRATION = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;

    @OneToOne(fetch = FetchType.EAGER, targetEntity = User.class)
    @JoinColumn(nullable = false, name = "user_id", foreignKey = @ForeignKey(name = "FK_VERIFY_USER"))
    private User user;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @CreationTimestamp
    private LocalDateTime datePublished;

    @UpdateTimestamp
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedDated;

    private LocalDateTime expiryDate;

    private String tokenType;

    @Value("${trailiva.app.jwtRefreshExpirationMs}")
    long refreshTokenExpiryTimeInSec;

    public Token(String token, User user, String tokenType) {
        this.token = token;
        this.user = user;
        this.tokenType = tokenType;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }

    private LocalDateTime calculateExpiryDate(long expiryTimeInHours){
        return LocalDateTime.now().plusHours(expiryTimeInHours);
    }

    public void updateToken(String code){
        this.token = code;
        this.tokenType = REFRESH.toString();
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }

    public Token(User user){
        this.token = UUID.randomUUID().toString();
        this.tokenType = REFRESH_TOKEN.toString();
        this.expiryDate = calculateExpiryDate(refreshTokenExpiryTimeInSec);
    }

    public void updateToken(String code, String tokenType){
        this.token = code;
        this.tokenType = tokenType;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }
}
