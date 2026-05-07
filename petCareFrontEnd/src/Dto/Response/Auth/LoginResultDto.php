<?php

namespace App\Dto\Response\Auth;

class LoginResultDto
{
    public function __construct(
        private string $token,
        private string $userId,
        private string $nomeUsuario,
        private string $email,
        private string $nivelAcesso,
        private string $perfilId,
        private string $tipoPerfil,
    ) {}

    public static function fromApiResponse(array $data): self
    {
        return new self(
            token: $data['token'],
            userId: $data['user']['id'] ?? '',
            nomeUsuario: $data['user']['nomeUsuario'] ?? '',
            email: $data['user']['email'] ?? '',
            nivelAcesso: $data['user']['nivelAcesso'] ?? '',
            perfilId: $data['perfilId'] ?? '',
            tipoPerfil: $data['tipoPerfil'] ?? '',
        );
    }

    public function getToken(): string
    {
        return $this->token;
    }

    public function getUserId(): string
    {
        return $this->userId;
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
}
