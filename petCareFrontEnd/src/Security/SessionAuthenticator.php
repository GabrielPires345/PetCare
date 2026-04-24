<?php

namespace App\Security;

use App\Security\User\SessionUser;
use Symfony\Component\HttpFoundation\RedirectResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpFoundation\Session\SessionInterface;
use Symfony\Component\Routing\Generator\UrlGeneratorInterface;
use Symfony\Component\Security\Core\Authentication\Token\TokenInterface;
use Symfony\Component\Security\Core\Exception\AuthenticationException;
use Symfony\Component\Security\Http\Authenticator\AbstractAuthenticator;
use Symfony\Component\Security\Http\Authenticator\Passport\Badge\UserBadge;
use Symfony\Component\Security\Http\Authenticator\Passport\SelfValidatingPassport;

class SessionAuthenticator extends AbstractAuthenticator
{
    public function __construct(
        private SessionInterface $session,
        private UrlGeneratorInterface $urlGenerator,
    ) {}

    public function supports(Request $request): ?bool
    {
        return true;
    }

    public function authenticate(Request $request): SelfValidatingPassport
    {
        $jwt = $this->session->get('jwt_token');
        $userData = $this->session->get('user');

        if (!$jwt || !$userData) {
            throw new AuthenticationException('No JWT token in session.');
        }

        $email = $userData['email'] ?? $userData['nomeUsuario'] ?? '';

        return new SelfValidatingPassport(
            new UserBadge($email, function (string $email) use ($userData, $jwt) {
                return new SessionUser(
                    id: $userData['id'] ?? '',
                    nomeUsuario: $userData['nomeUsuario'] ?? '',
                    email: $email,
                    nivelAcesso: $userData['nivelAcesso'] ?? '',
                    perfilId: $this->session->get('perfilId', ''),
                    tipoPerfil: $this->session->get('tipoPerfil', ''),
                    jwtToken: $jwt,
                );
            })
        );
    }

    public function onAuthenticationSuccess(Request $request, TokenInterface $token, string $firewallName): ?Response
    {
        return null;
    }

    public function onAuthenticationFailure(Request $request, AuthenticationException $exception): ?Response
    {
        return new RedirectResponse(
            $this->urlGenerator->generate('app_login_form')
        );
    }
}
