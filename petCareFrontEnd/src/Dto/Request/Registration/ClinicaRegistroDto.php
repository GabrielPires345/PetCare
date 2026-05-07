<?php

namespace App\Dto\Request\Registration;

class ClinicaRegistroDto
{
    public function __construct(
        private ?string $nomeUsuario,
        private ?string $email,
        private ?string $senha,
        private ?string $confirmaSenha,
        private ?string $nomeClinica,
        private ?string $razaoSocial,
        private ?string $cnpj,
    ) {}

    public function toArray(): array
    {
        return array_filter([
            'nomeUsuario' => $this->nomeUsuario,
            'email' => $this->email,
            'senha' => $this->senha,
            'confirmaSenha' => $this->confirmaSenha,
            'nomeClinica' => $this->nomeClinica,
            'razaoSocial' => $this->razaoSocial,
            'cnpj' => $this->cnpj,
        ], fn($v) => $v !== null);
    }

    public function isValid(): bool
    {
        return !empty($this->nomeUsuario)
            && !empty($this->email)
            && !empty($this->senha)
            && !empty($this->confirmaSenha)
            && !empty($this->nomeClinica)
            && !empty($this->razaoSocial)
            && !empty($this->cnpj);
    }
}
