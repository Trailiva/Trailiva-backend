package com.trailiva.data.repository;

import com.trailiva.data.model.Role;
import com.trailiva.data.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
}
