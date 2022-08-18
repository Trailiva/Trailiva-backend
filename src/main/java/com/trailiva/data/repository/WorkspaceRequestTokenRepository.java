package com.trailiva.data.repository;

import com.trailiva.data.model.WorkspaceRequestToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkspaceRequestTokenRepository extends JpaRepository<WorkspaceRequestToken, Long> {
}
