package com.poker.poker_.component;

import com.poker.poker_.entity.ERole;
import com.poker.poker_.entity.Role;
import com.poker.poker_.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            jdbcTemplate.execute("ALTER TABLE users DROP COLUMN password");
            System.out.println("Dropped old 'password' column to fix schema.");
        } catch (Exception e) {
            // Column might not exist, which is fine
        }

        if (roleRepository.findByName(ERole.ROLE_USER).isEmpty()) {
            roleRepository.save(new Role(null, ERole.ROLE_USER));
            System.out.println("Inserted ROLE_USER into database.");
        }
        
        if (roleRepository.findByName(ERole.ROLE_ADMIN).isEmpty()) {
            roleRepository.save(new Role(null, ERole.ROLE_ADMIN));
            System.out.println("Inserted ROLE_ADMIN into database.");
        }
    }
}
