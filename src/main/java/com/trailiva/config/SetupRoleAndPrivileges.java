package com.trailiva.config;

import com.trailiva.data.model.Role;
import com.trailiva.data.model.User;
import com.trailiva.data.repository.RoleRepository;
import com.trailiva.data.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Configuration
public class SetupRoleAndPrivileges implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private  RoleRepository roleRepository;

    @Autowired
    private  UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private boolean alreadySetup;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
            if (alreadySetup) return;
            createRoleIfNotFound("ROLE_ADMIN");
            createRoleIfNotFound("ROLE_USER");

        Role adminRole = roleRepository.findByName("ROLE_ADMIN");
        User user = new User();
        user.setFirstName("admin");
        user.setLastName("user");
        user.setPassword(passwordEncoder.encode("admin"));
        user.setEmail("admin@gmail.com");
        user.setRoles(List.of(adminRole));
        user.setEnabled(true);
        userRepository.save(user);
        alreadySetup = true;
    }

    @Transactional
    void createRoleIfNotFound(String roleName) {
        Role role = roleRepository.findByName(roleName);
        if (role == null){
            role = new Role(roleName);
            roleRepository.save(role);
        }
    }
}
