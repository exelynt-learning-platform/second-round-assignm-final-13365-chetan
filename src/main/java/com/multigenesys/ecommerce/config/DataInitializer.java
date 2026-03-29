package com.multigenesys.ecommerce.config;

import com.multigenesys.ecommerce.entity.Role;
import com.multigenesys.ecommerce.entity.User;
import com.multigenesys.ecommerce.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        userRepository.findByEmail("admin@multigenesys.com").orElseGet(() -> {
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail("admin@multigenesys.com");
            admin.setPassword(passwordEncoder.encode("Admin@12345"));
            admin.setRole(Role.ADMIN);
            return userRepository.save(admin);
        });
    }
}
