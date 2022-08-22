package com.trailiva.data.repository;

import com.trailiva.data.model.ProjectRequestToken;
import com.trailiva.data.model.WorkspaceRequestToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectRequestTokenRepository extends JpaRepository<ProjectRequestToken, Long> {
    Optional<ProjectRequestToken> findByTokenAndTokenType(String token, String tokenType);
}
