package com.trailiva.data.repository;

import com.trailiva.data.model.PersonalWorkspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PersonalWorkspaceRepository extends JpaRepository<PersonalWorkspace, Long>, JpaSpecificationExecutor<PersonalWorkspace> {
    boolean existsByName(String name);
}
