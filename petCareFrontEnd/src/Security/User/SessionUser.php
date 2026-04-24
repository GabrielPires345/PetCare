<?php

namespace App\Security\User;

use Symfony\Component\Security\Core\User\PasswordAuthenticatedUserInterface;
use Symfony\Component\Security\Core\User\UserInterface;

class SessionUser implements UserInterface, PasswordAuthenticatedUserInterface
{
    public function __construct(
        private string $id,
        private string $nomeUsuario,
        private string $email,
        private string $nivelAcesso,
        private string $perfilId,
        private string $tipoPerfil,
        private string $jwtToken,
    ) {}

    public function getUserIdentifier(): string
    {
        return $this->email;
    }

    public function getRoles(): array
    {
        return ['ROLE_USER', 'ROLE_' . $this->nivelAcesso];
    }

    public function getPassword(): ?string
    {
        return null;
    }

    public function eraseCredentials(): void
    {
    }

    public function getId(): string
    {
        return $this->id;
    }

    public function getNomeUsuario(): string
    {
        return $this->nomeUsuario;
    }

    public function getEmail(): string
    {
        return $this->email;
    }

    public function getNivelAcesso(): string
    {
        return $this->nivelAcesso;
    }

    public function getPerfilId(): string
    {
        return $this->perfilId;
    }

    public function getTipoPerfil(): string
    {
        return $this->tipoPerfil;
    }

    public function getJwtToken(): string
    {
        return $this->jwtToken;
    }
}
