<?php

namespace App\Dto;

class UserRegisterDto
{
    private mixed $email;
    private mixed $password;

    public function __construct($data){
        $this->email = $data['email'] ?? null;
        $this->password = $data['password'] ?? null;
    }


    public function getEmail(): string
    {
        return $this->email;
    }

    public function setEmail(string $email): void
    {
        $this->email = $email;
    }

    public function getPassword(): string
    {
        return $this->password;
    }

    public function setPassword(string $password): void
    {
        $this->password = $password;
    }
}
