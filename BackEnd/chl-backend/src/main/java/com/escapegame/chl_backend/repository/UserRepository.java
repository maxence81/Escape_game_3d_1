package com.escapegame.chl_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.escapegame.chl_backend.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Genera SQL: SELECT * FROM users WHERE email = ?
    // Usado en AuthController para el Login
    Optional<User> findByEmail(String email);

    // Genera SQL: SELECT COUNT(*) > 0 FROM users WHERE email = ?
    // Usado en AuthController para evitar duplicados en el Registro
    Boolean existsByEmail(String email);
}