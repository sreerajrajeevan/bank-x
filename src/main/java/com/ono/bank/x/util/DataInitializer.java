package com.ono.bank.x.util;

import com.ono.bank.x.model.UserRole;
import com.ono.bank.x.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public DataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Initialize roles if they don't exist
        if (!roleRepository.existsById(1L)) {
            roleRepository.save(new UserRole("USER"));
            roleRepository.save(new UserRole("ADMIN"));
        }
    }
}
