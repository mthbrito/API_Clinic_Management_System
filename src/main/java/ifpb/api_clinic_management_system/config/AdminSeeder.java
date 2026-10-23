package ifpb.api_clinic_management_system.config;

import ifpb.api_clinic_management_system.model.entity.Role;
import ifpb.api_clinic_management_system.model.entity.User;
import ifpb.api_clinic_management_system.model.enumeration.RoleType;
import ifpb.api_clinic_management_system.repository.RoleRepository;
import ifpb.api_clinic_management_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.seed.email:}")
    private String seedEmail;

    @Value("${admin.seed.password:}")
    private String seedPassword;

    @Override
    @Transactional
    public void run(String... args) {
        if (seedEmail == null || seedEmail.isBlank()
                || seedPassword == null || seedPassword.isBlank()) {
            log.info("Admin seed skipped: ADMIN_SEED_EMAIL/ADMIN_SEED_PASSWORD not set.");
            return;
        }

        if (userRepository.existsByEmail(seedEmail)) {
            log.info("Admin seed skipped: a user with email {} already exists.", seedEmail);
            return;
        }

        Role adminRole = roleRepository.findByName(RoleType.ROLE_ADMIN)
                .orElseThrow(() -> new IllegalStateException(
                        "ROLE_ADMIN not found - check that Flyway migration V2__seed_roles.sql ran."));

        User admin = User.builder()
                .email(seedEmail)
                .password(passwordEncoder.encode(seedPassword))
                .enabled(true)
                .roles(Set.of(adminRole))
                .build();

        userRepository.save(admin);
        log.info("Seeded initial admin user with email: {}", seedEmail);
    }
}