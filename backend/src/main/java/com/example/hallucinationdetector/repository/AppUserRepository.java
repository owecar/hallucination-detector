package com.example.hallucinationdetector.repository;

import com.example.hallucinationdetector.model.AppUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByUsername(String username);

    long countByRole(String role);
}
