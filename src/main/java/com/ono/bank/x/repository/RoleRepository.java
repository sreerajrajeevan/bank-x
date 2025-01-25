package com.ono.bank.x.repository;
import com.ono.bank.x.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface RoleRepository extends JpaRepository<UserRole, Long> {
    Optional<UserRole> findByName(String name);  // Find role by name, like "USER", "ADMIN"
}

