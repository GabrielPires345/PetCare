<?php

namespace App\Dto\Request\Registration;

class ClienteRegistroDto
{
    public function __construct(
        private ?string $nomeUsuario,
        private ?string $email,
        private ?string $senha,
        private ?string $confirmaSenha,
        private ?string $nomeCompleto,
        private ?string $cpf,
        private ?string $dataNascimento,
        private ?PetDto $pet,
    ) {}

    public function toArray(): array
    {
        $data = [
            'nomeUsuario' => $this->nomeUsuario,
            'email' => $this->email,
            'senha' => $this->senha,
            'confirmaSenha' => $this->confirmaSenha,
            'nomeCompleto' => $this->nomeCompleto,
            'cpf' => $this->cpf,
            'dataNascimento' => $this->dataNascimento,
        ];

        if ($this->pet !== null && $this->pet->hasNome()) {
            $data['pet'] = $this->pet->toArray();
        }

        return array_filter($data, fn($v) => $v !== null);
    }

    public function isValid(): bool
    {
        return !empty($this->nomeUsuario)
            && !empty($this->email)
            && !empty($this->senha)
            && !empty($this->confirmaSenha)
            && !empty($this->nomeCompleto)
            && !empty($this->cpf)
            && !empty($this->dataNascimento);
    }
}
