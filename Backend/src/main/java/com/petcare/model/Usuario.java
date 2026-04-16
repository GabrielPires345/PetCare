package com.petcare.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE usuario SET deleted_at = NOW() WHERE id_usuario = ?")
@SQLRestriction("deleted_at IS NULL")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_usuario")
    private UUID id;

    @Column(name = "email", nullable = false, unique = true)
    @Email
    private String email;

    @Column(name = "username", nullable = false, unique = true, length = 10)
    @NotBlank
    @Size(max = 10, message = "Nome de usuário deve ter no máximo 10 caracteres")
    private String nomeUsuario;

    @Column(name = "password_hash", nullable = false)
    @NotBlank
    private String senhaHash;

    @Column(name = "nivel_acesso", nullable = false)
    @NotBlank
    private String nivelAcesso;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}