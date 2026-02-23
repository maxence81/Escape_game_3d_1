package com.escapegame.chl_backend.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.escapegame.chl_backend.dto.request.RegisterRequest;
import com.escapegame.chl_backend.model.Player;
import com.escapegame.chl_backend.model.Role;
import com.escapegame.chl_backend.repository.PlayerRepository;
import com.escapegame.chl_backend.repository.UserRepository;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Erreur: L'email est déjà utilisé!");
        }

        Player player = new Player();
        player.setEmail(request.getEmail());
        player.setPassword(passwordEncoder.encode(request.getPassword()));
        player.setRole(Role.USER); 
        player.setPrenom(request.getPrenom());
        player.setNom(request.getNom());
        player.setPseudo(request.getPseudo());
        player.setEtablissement(request.getEtablissement());
        player.setDateNaissance(request.getDateNaissance());
        player.setDateInscription(LocalDate.now());

        playerRepository.save(player);
    }
}