package com.escapegame.chl_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.escapegame.chl_backend.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    
    // Solo deben existir estos dos. 
    // ¡Spring ya sabe buscar por ID por defecto, no hay que escribir findById!
    
    Optional<User> findByEmail(String email);
    
    Boolean existsByEmail(String email);
}