package com.trailiva.data.repository;

import com.trailiva.data.model.OfficialWorkspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OfficialWorkspaceRepository extends JpaRepository<OfficialWorkspace, Long>, JpaSpecificationExecutor<OfficialWorkspace> {
    boolean existsByName(String name);
}
