<?php

namespace App\Twig;

use App\Service\Auth\AuthService;
use Twig\Extension\AbstractExtension;
use Twig\TwigFunction;

class AppExtension extends AbstractExtension
{
    public function __construct(
        private AuthService $authService,
    ) {}

    public function getFunctions(): array
    {
        return [
            new TwigFunction('is_authenticated', [$this, 'isAuthenticated']),
            new TwigFunction('user_name', [$this, 'getUserName']),
            new TwigFunction('user_tipo', [$this, 'getUserTipo']),
        ];
    }

    public function isAuthenticated(): bool
    {
        return $this->authService->isAuthenticated();
    }

    public function getUserName(): string
    {
        $user = $this->authService->getUser();
        return $user['nomeUsuario'] ?? 'Usuário';
    }

    public function getUserTipo(): string
    {
        return $this->authService->getTipoPerfil() ?? 'Membro';
    }
}
