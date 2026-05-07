<?php

namespace App\Dto\Request\Auth;

class LoginRequestDto
{
    private ?string $email;
    private ?string $senha;

    public function __construct(array $data)
    {
        $this->email = $data['email'] ?? null;
        $this->senha = $data['senha'] ?? null;
    }

    public function getEmail(): ?string
    {
        return $this->email;
    }

    public function getSenha(): ?string
    {
        return $this->senha;
    }

    public function isValid(): bool
    {
        return !empty($this->email) && !empty($this->senha);
    }
}
