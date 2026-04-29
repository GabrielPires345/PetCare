<?php

namespace App\Dto\Request\Registration;

class VeterinarioRegistroDto
{
    public function __construct(
        private ?string $nomeUsuario,
        private ?string $email,
        private ?string $senha,
        private ?string $confirmaSenha,
        private ?string $nome,
        private ?string $crmv,
        private array   $especialidadeIds = [],
    ) {}

    public function toArray(): array
    {
        $data = [
            'nomeUsuario' => $this->nomeUsuario,
            'email' => $this->email,
            'senha' => $this->senha,
            'confirmaSenha' => $this->confirmaSenha,
            'nome' => $this->nome,
            'crmv' => $this->crmv,
            'especialidadeIds' => $this->especialidadeIds,
        ];
        return array_filter($data, fn($v) => $v !== null);
    }

    public function isValid(): bool
    {
        return !empty($this->nomeUsuario)
            && !empty($this->email)
            && !empty($this->senha)
            && !empty($this->confirmaSenha)
            && !empty($this->nome)
            && !empty($this->crmv);
    }
}
