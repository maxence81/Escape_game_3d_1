package com.escapegame.chl_backend.controller;

import com.escapegame.chl_backend.dto.request.LoginRequest;
import com.escapegame.chl_backend.dto.request.RegisterRequest;
import com.escapegame.chl_backend.dto.response.JwtResponse;
import com.escapegame.chl_backend.dto.response.MessageResponse;
import com.escapegame.chl_backend.model.Role;
import com.escapegame.chl_backend.model.User;
import com.escapegame.chl_backend.repository.UserRepository;
import com.escapegame.chl_backend.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        
        // 1. Autenticar usando Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // 2. Generar el Token JWT
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        // 3. Buscar el usuario para devolver sus datos (rol, nombre, etc)
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Error: Usuario no encontrado."));

        // Retornamos el token y los datos básicos
        return ResponseEntity.ok(new JwtResponse(
                jwt, 
                user.getId(), 
                user.getEmail(), 
                user.getRole().name(),
                user.getFirstName()
        ));
    }

    // REGISTRO
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest signUpRequest) {
        // 1. Validar si el email ya existe
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: El email ya está en uso!"));
        }

        // 2. Crear nuevo usuario
        User user = new User();
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());
        user.setEmail(signUpRequest.getEmail());
        user.setBirthDate(signUpRequest.getBirthDate()); 
        user.setPassword(encoder.encode(signUpRequest.getPassword())); // Encriptar password
        user.setRole(Role.USER); // Por defecto todos son USER

        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Usuario registrado exitosamente!"));
    }
}
