package com.escapegame.chl_backend.model;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users") // "user" es una palabra reservada en SQL, mejor usar plural
@Data // Genera Getters, Setters, ToString, etc. automáticamente
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password; // Se guardará encriptada (BCrypt)

    private String firstName;
    private String lastName;

    private LocalDate birthDate; // Necesario para calcular la edad media

    @Enumerated(EnumType.STRING)
    private Role role; // USER o ADMIN

    // Relación: Un usuario tiene muchas partidas jugadas
    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GameSession> sessions;
    // Método auxiliar (no se guarda en BD) para el gráfico de "Distribución por Edad"
    public int getAge() {
        if (birthDate == null) return 0;
        return Period.between(this.birthDate, LocalDate.now()).getYears();
    }
}
