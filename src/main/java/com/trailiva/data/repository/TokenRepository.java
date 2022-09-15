package com.trailiva.data.repository;

import com.trailiva.data.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByTokenAndTokenType(String verificationCode, String tokenType);
    @Modifying
    @Query(nativeQuery = true, value = "delete from token t where CURRENT_TIMESTAMP > t.expiry_date")
    void deleteExpiredToken();
}
