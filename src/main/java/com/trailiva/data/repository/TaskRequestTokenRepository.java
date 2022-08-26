package com.trailiva.data.repository;

import com.trailiva.data.model.TaskRequestToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskRequestTokenRepository extends JpaRepository<TaskRequestToken, Long> {
    Optional<TaskRequestToken> findByTokenAndTokenType(String token, String tokenType);
}
