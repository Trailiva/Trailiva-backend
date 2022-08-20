package com.trailiva.data.repository;

import com.trailiva.data.model.OfficialWorkspace;
import com.trailiva.data.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface OfficialWorkspaceRepository extends JpaRepository<OfficialWorkspace, Long>, JpaSpecificationExecutor<OfficialWorkspace> {
    boolean existsByName(String name);
    Optional<OfficialWorkspace> findByCreator(User user);
}
