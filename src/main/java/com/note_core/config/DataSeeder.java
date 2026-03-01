package com.note_core.config;

import com.note_core.plan.Plan;
import com.note_core.plan.PlanRepository;
import com.note_core.user.Role;
import com.note_core.user.RoleRepository;
import com.note_core.user.User;
import com.note_core.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
public class DataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PlanRepository planRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository,
                      RoleRepository roleRepository,
                      PlanRepository planRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.planRepository = planRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedAdmin();
        seedUser();
    }

    private void seedAdmin() {
        String email = "admin@note.com";
        if (userRepository.existsByEmail(email)) {
            log.info("Admin user already exists, skipping seed");
            return;
        }

        Role superAdminRole = roleRepository.findByName("SUPER_ADMIN")
                .orElseThrow(() -> new IllegalStateException("SUPER_ADMIN role not found — run migrations first"));

        User admin = new User();
        admin.setName("Super Admin");
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRoles(Set.of(superAdminRole));

        userRepository.save(admin);
        log.info("Seeded admin user: {}", email);
    }

    private void seedUser() {
        String email = "user@note.com";
        if (userRepository.existsByEmail(email)) {
            log.info("Default user already exists, skipping seed");
            return;
        }

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("USER role not found — run migrations first"));

        Plan freePlan = planRepository.findByName("FREE")
                .orElseThrow(() -> new IllegalStateException("FREE plan not found — run migrations first"));

        User user = new User();
        user.setName("Default User");
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("user1234"));
        user.setRoles(Set.of(userRole));
        user.setPlan(freePlan);

        userRepository.save(user);
        log.info("Seeded default user: {}", email);
    }
}
