<?php

namespace App\Service\Auth;

use App\Dto\Request\Auth\LoginRequestDto;
use App\Dto\Response\Auth\LoginResultDto;
use App\Exception\AuthenticationException;
use App\Exception\ValidationException;
use App\Service\Api\BackendApiService;
use Symfony\Component\HttpFoundation\Session\SessionInterface;

class AuthService
{
    private const SESSION_KEY_JWT = 'jwt_token';
    private const SESSION_KEY_USER = 'user';
    private const SESSION_KEY_PID = 'perfilId';
    private const SESSION_KEY_TYPE = 'tipoPerfil';

    public function __construct(
        private BackendApiService $api,
        private SessionInterface $session,
    ) {}

    public function login(LoginRequestDto $dto): LoginResultDto
    {
        if (!$dto->isValid()) {
            throw new ValidationException('Preencha todos os campos.');
        }

        $result = $this->api->login($dto->getEmail(), $dto->getSenha());

        if (!$result->isSuccess()) {
            throw new AuthenticationException(
                $result->getError() ?? 'Erro ao fazer login.',
                $result->getApiErrorCode()
            );
        }

        $loginResult = LoginResultDto::fromApiResponse($result->getData());
        $this->persistSession($loginResult);

        return $loginResult;
    }

    public function logout(): void
    {
        $this->session->invalidate();
    }

    public function verifyEmail(string $token): void
    {
        if (empty($token)) {
            throw new ValidationException('Token de verificação inválido.');
        }

        $result = $this->api->verificarEmail($token);

        if (!$result->isSuccess()) {
            throw new AuthenticationException(
                $result->getError() ?? 'Erro na verificação.',
                $result->getApiErrorCode()
            );
        }
    }

    public function isAuthenticated(): bool
    {
        return $this->session->has(self::SESSION_KEY_JWT);
    }

    public function getToken(): ?string
    {
        return $this->session->get(self::SESSION_KEY_JWT);
    }

    public function getUser(): ?array
    {
        return $this->session->get(self::SESSION_KEY_USER);
    }

    public function getTipoPerfil(): ?string
    {
        return $this->session->get(self::SESSION_KEY_TYPE);
    }

    private function persistSession(LoginResultDto $dto): void
    {
        $this->session->set(self::SESSION_KEY_JWT, $dto->getToken());
        $this->session->set(self::SESSION_KEY_USER, [
            'id' => $dto->getUserId(),
            'nomeUsuario' => $dto->getNomeUsuario(),
            'email' => $dto->getEmail(),
            'nivelAcesso' => $dto->getNivelAcesso(),
        ]);
        $this->session->set(self::SESSION_KEY_PID, $dto->getPerfilId());
        $this->session->set(self::SESSION_KEY_TYPE, $dto->getTipoPerfil());
    }
}
