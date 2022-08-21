package com.trailiva.data.repository;

import com.trailiva.data.model.WorkspaceRequestToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkspaceRequestTokenRepository extends JpaRepository<WorkspaceRequestToken, Long> {
    Optional<WorkspaceRequestToken> findByTokenAndTokenType(String token, String tokenType);
}
