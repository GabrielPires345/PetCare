<?php

namespace App\Dto\Cadastro;

class ClienteCadastroDto
{
    private ?string $email;
    private ?string $password;
    private ?string $confirmacaoSenha;
    private ?string $nomeCompleto;
    private ?string $cpf;
    private ?string $dataNascimento;

    public function __construct(array $data)
    {
        $this->email = $data['email'] ?? null;
        $this->password = $data['senha'] ?? null;
        $this->confirmacaoSenha = $data['confirmar_senha'] ?? null;
        $this->nomeCompleto = $data['nomeCompleto'] ?? null;
        $this->cpf = $data['CPF'] ?? null;
        $this->dataNascimento = $data['data_nascimento'] ?? null;
    }

    public function getEmail(): ?string
    {
        return $this->email;
    }

    public function setEmail(?string $email): void
    {
        $this->email = $email;
    }

    public function getPassword(): ?string
    {
        return $this->password;
    }

    public function setPassword(?string $password): void
    {
        $this->password = $password;
    }

    public function getConfirmacaoSenha(): ?string
    {
        return $this->confirmacaoSenha;
    }

    public function setConfirmacaoSenha(?string $confirmacaoSenha): void
    {
        $this->confirmacaoSenha = $confirmacaoSenha;
    }

    public function getNomeCompleto(): ?string
    {
        return $this->nomeCompleto;
    }

    public function setNomeCompleto(?string $nomeCompleto): void
    {
        $this->nomeCompleto = $nomeCompleto;
    }

    public function getCpf(): ?string
    {
        return $this->cpf;
    }

    public function setCpf(?string $cpf): void
    {
        $this->cpf = $cpf;
    }

    public function getDataNascimento(): ?string
    {
        return $this->dataNascimento;
    }

    public function setDataNascimento(?string $dataNascimento): void
    {
        $this->dataNascimento = $dataNascimento;
    }
}
