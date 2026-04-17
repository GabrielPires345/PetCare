<?php

namespace App\Services;

use App\Dto\Cadastro\ClienteCadastroDto;

class UserServices
{
    public function validateForm(ClienteCadastroDto $dto): bool
    {
        if (empty($dto->getEmail()) || empty($dto->getPassword()) || $this->isStrongPassword($dto->getPassword())) {
            return false;
        }

        return true;
    }

    private function isStrongPassword(string $password): bool
    {
        return preg_match('/^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[^a-zA-Z0-9]).+$/', $password) === 1;
    }
}
