<?php

namespace App\Dto\Request\Registration;

class PetDto
{
    public function __construct(
        private ?string $nome,
        private ?string $especie,
        private ?string $sexo,
        private ?float  $peso,
        private ?string $dataNascimento,
        private bool    $castrado = false,
    ) {}

    public static function fromArray(array $data): self
    {
        return new self(
            nome: $data['nome'] ?? null,
            especie: $data['especie'] ?? null,
            sexo: $data['sexo'] ?? null,
            peso: isset($data['peso']) ? (float) $data['peso'] : null,
            dataNascimento: $data['dataNascimento'] ?? null,
            castrado: isset($data['castrado']) && $data['castrado'] === 'true',
        );
    }

    public function toArray(): array
    {
        return array_filter([
            'nome' => $this->nome,
            'especie' => $this->especie,
            'sexo' => $this->sexo,
            'peso' => $this->peso,
            'dataNascimento' => $this->dataNascimento,
            'castrado' => $this->castrado,
        ], fn($v) => $v !== null);
    }

    public function hasNome(): bool
    {
        return !empty($this->nome);
    }
}
