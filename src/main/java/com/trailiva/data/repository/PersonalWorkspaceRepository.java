package com.trailiva.data.repository;

import com.trailiva.data.model.OfficialWorkspace;
import com.trailiva.data.model.PersonalWorkspace;
import com.trailiva.data.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface PersonalWorkspaceRepository extends JpaRepository<PersonalWorkspace, Long>, JpaSpecificationExecutor<PersonalWorkspace> {
    boolean existsByName(String name);
    Optional<PersonalWorkspace> findByCreator(User user);

}
