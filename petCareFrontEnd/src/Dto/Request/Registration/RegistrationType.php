<?php

namespace App\Dto\Request\Registration;

enum RegistrationType: string
{
    case CLIENTE = 'CLIENTE';
    case VETERINARIO = 'VETERINARIO';
    case CLINICA = 'CLINICA';
}
