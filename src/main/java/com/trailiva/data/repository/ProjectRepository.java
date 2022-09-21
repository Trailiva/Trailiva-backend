package com.trailiva.data.repository;

import com.trailiva.data.model.Project;
import com.trailiva.data.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> { }
