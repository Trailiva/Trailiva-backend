package com.trailiva.data.repository;

import com.trailiva.data.model.Priority;
import com.trailiva.data.model.PriorityField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskPriorityRepository extends JpaRepository<PriorityField, Long> {
    boolean existsByPriority(Priority priority);
}
