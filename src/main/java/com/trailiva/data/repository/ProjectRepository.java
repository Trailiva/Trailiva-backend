package com.trailiva.data.repository;

import com.trailiva.data.model.Project;
import com.trailiva.data.model.Task;
import com.trailiva.web.exceptions.WorkspaceException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
