package com.trailiva.event;

import com.trailiva.data.model.Role;
import com.trailiva.data.model.User;
import com.trailiva.data.repository.RoleRepository;
import com.trailiva.data.repository.UserRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Configuration
public class SetupRoleAndPrivileges implements ApplicationListener<ContextRefreshedEvent> {

    private final   RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    boolean alreadySetup = false;

    public SetupRoleAndPrivileges(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
            if (alreadySetup) return;
            createRoleIfNotFound("ROLE_ADMIN");
            createRoleIfNotFound("ROLE_USER");
            createRoleIfNotFound("ROLE_SUPER_MODERATOR");
            createRoleIfNotFound("ROLE_MODERATOR");


        Optional<Role> adminRole = roleRepository.findByName("ROLE_ADMIN");
        if (adminRole.isPresent()){
            User user = new User();
            user.setFirstName("admin");
            user.setLastName("user");
            user.setPassword(passwordEncoder.encode("admin"));
            user.setEmail("admin@gmail.com");
            user.setRoles(List.of(adminRole.get()));
            user.setEnabled(true);
            userRepository.save(user);
        }

    }

    @Transactional
    void createRoleIfNotFound(String roleName) {
        Optional<Role> role = roleRepository.findByName(roleName);
        if (role.isEmpty()){
            roleRepository.save(new Role(roleName));
        }
    }
}
