<?php

namespace App\Security\User;

use Symfony\Component\HttpFoundation\Session\SessionInterface;
use Symfony\Component\Security\Core\Exception\UserNotFoundException;
use Symfony\Component\Security\Core\User\UserInterface;
use Symfony\Component\Security\Core\User\UserProviderInterface;

class SessionUserProvider implements UserProviderInterface
{
    public function __construct(
        private SessionInterface $session,
    ) {}

    public function loadUserByIdentifier(string $identifier): UserInterface
    {
        $userData = $this->session->get('user');
        $jwt = $this->session->get('jwt_token');

        if (!$userData || !$jwt) {
            throw new UserNotFoundException('No authenticated user in session.');
        }

        return new SessionUser(
            id: $userData['id'] ?? '',
            nomeUsuario: $userData['nomeUsuario'] ?? '',
            email: $identifier,
            nivelAcesso: $userData['nivelAcesso'] ?? '',
            perfilId: $this->session->get('perfilId', ''),
            tipoPerfil: $this->session->get('tipoPerfil', ''),
            jwtToken: $jwt,
        );
    }

    public function refreshUser(UserInterface $user): UserInterface
    {
        if (!$user instanceof SessionUser) {
            throw new \InvalidArgumentException('Invalid user class.');
        }
        return $this->loadUserByIdentifier($user->getUserIdentifier());
    }

    public function supportsClass(string $class): bool
    {
        return $class === SessionUser::class;
    }
}
