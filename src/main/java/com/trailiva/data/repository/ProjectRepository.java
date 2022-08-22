package com.trailiva.data.repository;

import com.trailiva.data.model.PersonalWorkspace;
import com.trailiva.data.model.Project;
import com.trailiva.data.model.Task;
import com.trailiva.data.model.User;
import com.trailiva.web.exceptions.WorkspaceException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findByCreator(User user);
}
