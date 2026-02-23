package com.escapegame.chl_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.escapegame.chl_backend.dto.request.LoginRequest;
import com.escapegame.chl_backend.dto.request.RegisterRequest;
import com.escapegame.chl_backend.dto.response.JwtResponse;
import com.escapegame.chl_backend.dto.response.MessageResponse;
import com.escapegame.chl_backend.model.User;
import com.escapegame.chl_backend.repository.UserRepository;
import com.escapegame.chl_backend.security.JwtUtils;
import com.escapegame.chl_backend.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthService authService;

    // Inyectamos el repositorio para poder sacar el ID del usuario
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        // Autenticar al usuario
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // Generar Token JWT
        String jwt = jwtUtils.generateJwtToken(authentication);

        // Obtener detalles del usuario (AQUÍ ESTABA EL ERROR, DEBE SER UserDetails)
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Obtener el rol
        String role = userDetails.getAuthorities().iterator().next().getAuthority();

        // Buscar el usuario en la BD para sacar su ID real
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return ResponseEntity.ok(new JwtResponse(
                jwt,
                user.getId_utilisateur(), // Ahora sí tenemos el ID correcto
                userDetails.getUsername(),
                role
        ));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest signUpRequest) {
        try {
            authService.registerUser(signUpRequest);
            return ResponseEntity.ok(new MessageResponse("¡Usuario registrado exitosamente!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}