<?php

namespace App\Security;

use App\Security\User\SessionUser;
use Symfony\Component\HttpFoundation\RedirectResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\RequestStack;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Generator\UrlGeneratorInterface;
use Symfony\Component\Security\Core\Authentication\Token\TokenInterface;
use Symfony\Component\Security\Core\Exception\AuthenticationException;
use Symfony\Component\Security\Http\Authenticator\AbstractAuthenticator;
use Symfony\Component\Security\Http\Authenticator\Passport\Badge\UserBadge;
use Symfony\Component\Security\Http\Authenticator\Passport\SelfValidatingPassport;

class SessionAuthenticator extends AbstractAuthenticator
{
    public function __construct(
        private RequestStack $requestStack,
        private UrlGeneratorInterface $urlGenerator,
    ) {}

    public function supports(Request $request): ?bool
    {
        // Only authenticate requests under /app (protected area)
        // Other routes (login, register, home) should not trigger authentication
        return str_starts_with($request->getPathInfo(), '/app');
    }

    public function authenticate(Request $request): SelfValidatingPassport
    {
        $session = $this->requestStack->getSession();
        $jwt = $session->get('jwt_token');
        $userData = $session->get('user');

        if (!$jwt || !$userData) {
            throw new AuthenticationException('No JWT token in session.');
        }

        $email = $userData['email'] ?? $userData['nomeUsuario'] ?? '';

        return new SelfValidatingPassport(
            new UserBadge($email, function (string $email) use ($userData, $jwt, $session) {
                return new SessionUser(
                    id: $userData['id'] ?? '',
                    nomeUsuario: $userData['nomeUsuario'] ?? '',
                    email: $email,
                    nivelAcesso: $userData['nivelAcesso'] ?? '',
                    perfilId: $session->get('perfilId', ''),
                    tipoPerfil: $session->get('tipoPerfil', ''),
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
