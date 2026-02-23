package com.escapegame.chl_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.escapegame.chl_backend.model.Enigma;

public interface EnigmaRepository extends JpaRepository<Enigma, Long> {
}
