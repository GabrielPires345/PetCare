<?php

namespace App\Service\Registration;

use App\Dto\Request\Registration\ClienteRegistroDto;
use App\Dto\Request\Registration\ClinicaRegistroDto;
use App\Dto\Request\Registration\PetDto;
use App\Dto\Request\Registration\RegistrationType;
use App\Dto\Request\Registration\VeterinarioRegistroDto;
use App\Exception\ApiException;
use App\Exception\ValidationException;
use App\Service\Api\BackendApiService;

class RegistrationService
{
    public function __construct(
        private BackendApiService $api,
    ) {}

    public function register(RegistrationType $tipo, array $formData): void
    {
        if (($formData['senha'] ?? '') !== ($formData['confirmaSenha'] ?? '')) {
            throw new ValidationException('As senhas não conferem.');
        }

        $result = match ($tipo) {
            RegistrationType::CLIENTE => $this->registerClient($formData),
            RegistrationType::VETERINARIO => $this->registerVet($formData),
            RegistrationType::CLINICA => $this->registerClinica($formData),
        };

        if (!$result->isSuccess()) {
            throw new ApiException(
                $result->getError() ?? 'Erro no registro.',
                $result->getApiErrorCode(),
                $result->getFieldErrors()
            );
        }
    }

    private function registerClient(array $formData): \App\Dto\Response\Api\ApiResponseDto
    {
        $petDto = null;
        if (!empty($formData['pet']['nome'])) {
            $petDto = PetDto::fromArray($formData['pet'] ?? []);
        }

        $dto = new ClienteRegistroDto(
            nomeUsuario: $formData['nomeUsuario'] ?? null,
            email: $formData['email'] ?? null,
            senha: $formData['senha'] ?? null,
            confirmaSenha: $formData['confirmaSenha'] ?? null,
            nomeCompleto: $formData['nomeCompleto'] ?? null,
            cpf: $formData['cpf'] ?? null,
            dataNascimento: $formData['dataNascimento'] ?? null,
            pet: $petDto,
        );

        if (!$dto->isValid()) {
            throw new ValidationException('Dados do cliente inválidos. Verifique os campos obrigatórios.');
        }

        return $this->api->registrarCliente($dto->toArray());
    }

    private function registerVet(array $formData): \App\Dto\Response\Api\ApiResponseDto
    {
        $dto = new VeterinarioRegistroDto(
            nomeUsuario: $formData['nomeUsuario'] ?? null,
            email: $formData['email'] ?? null,
            senha: $formData['senha'] ?? null,
            confirmaSenha: $formData['confirmaSenha'] ?? null,
            nome: $formData['nome'] ?? null,
            crmv: $formData['crmv'] ?? null,
        );

        if (!$dto->isValid()) {
            throw new ValidationException('Dados do veterinário inválidos. Verifique os campos obrigatórios.');
        }

        return $this->api->registrarVeterinario($dto->toArray());
    }

    private function registerClinica(array $formData): \App\Dto\Response\Api\ApiResponseDto
    {
        $dto = new ClinicaRegistroDto(
            nomeUsuario: $formData['nomeUsuario'] ?? null,
            email: $formData['email'] ?? null,
            senha: $formData['senha'] ?? null,
            confirmaSenha: $formData['confirmaSenha'] ?? null,
            nomeClinica: $formData['nomeClinica'] ?? null,
            razaoSocial: $formData['razaoSocial'] ?? null,
            cnpj: $formData['cnpj'] ?? null,
        );

        if (!$dto->isValid()) {
            throw new ValidationException('Dados da clínica inválidos. Verifique os campos obrigatórios.');
        }

        return $this->api->registrarClinica($dto->toArray());
    }
}
