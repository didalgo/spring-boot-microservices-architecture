package com.idalgo.daniel.authservice.config;

import com.idalgo.daniel.authservice.entity.Role;
import com.idalgo.daniel.authservice.entity.User;
import com.idalgo.daniel.authservice.repository.RoleRepository;
import com.idalgo.daniel.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Initialize default data on application startup.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initializeRoles();
        initializeUsers();
    }

    private void initializeRoles() {
        if (roleRepository.count() == 0) {
            Role userRole = Role.builder()
                    .name("ROLE_USER")
                    .description("Default user role")
                    .build();

            Role adminRole = Role.builder()
                    .name("ROLE_ADMIN")
                    .description("Administrator role")
                    .build();

            roleRepository.save(userRole);
            roleRepository.save(adminRole);

            log.info("Initialized default roles: ROLE_USER, ROLE_ADMIN");
        }
    }

    private void initializeUsers() {
        if (userRepository.count() == 0) {
            // Create admin user
            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseThrow(() -> new RuntimeException("ROLE_ADMIN not found"));

            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("ROLE_USER not found"));

            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(adminRole);
            adminRoles.add(userRole);

            User admin = User.builder()
                    .username("admin")
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("admin123"))
                    .firstName("Admin")
                    .lastName("User")
                    .enabled(true)
                    .roles(adminRoles)
                    .build();

            userRepository.save(admin);

            // Create regular user
            Set<Role> userRoles = new HashSet<>();
            userRoles.add(userRole);

            User user = User.builder()
                    .username("user")
                    .email("user@example.com")
                    .password(passwordEncoder.encode("user123"))
                    .firstName("Regular")
                    .lastName("User")
                    .enabled(true)
                    .roles(userRoles)
                    .build();

            userRepository.save(user);

            log.info("Initialized default users:");
            log.info("  - admin/admin123 (ROLE_ADMIN, ROLE_USER)");
            log.info("  - user/user123 (ROLE_USER)");
        }
    }
}