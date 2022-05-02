package com.trailiva.data.repository;

import com.trailiva.data.model.WorkSpace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkspaceRepository extends JpaRepository<WorkSpace, Long> {
    boolean existsByName(String name);
    boolean existsByReferenceName(String referenceName);
}
