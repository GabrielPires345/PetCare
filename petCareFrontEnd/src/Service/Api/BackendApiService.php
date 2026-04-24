<?php

namespace App\Service\Api;

use App\Dto\Response\Api\ApiResponseDto;
use Symfony\Contracts\HttpClient\HttpClientInterface;
use Symfony\Contracts\HttpClient\Exception\TransportExceptionInterface;

class BackendApiService
{
    private string $baseUrl;

    public function __construct(
        private HttpClientInterface $client,
        string $baseUrl,
    ) {
        $this->baseUrl = rtrim($baseUrl, '/');
    }

    public function login(string $email, string $senha): ApiResponseDto
    {
        return $this->post('/api/auth/login', [
            'email' => $email,
            'senha' => $senha,
        ]);
    }

    public function registrarCliente(array $payload): ApiResponseDto
    {
        return $this->post('/api/auth/registro', $payload);
    }

    public function registrarVeterinario(array $payload): ApiResponseDto
    {
        return $this->post('/api/auth/registro-veterinario', $payload);
    }

    public function registrarClinica(array $payload): ApiResponseDto
    {
        return $this->post('/api/auth/registro-clinica', $payload);
    }

    public function verificarEmail(string $token): ApiResponseDto
    {
        return $this->get('/api/auth/verificar-email?token=' . urlencode($token));
    }

    public function reenviarVerificacao(string $email): ApiResponseDto
    {
        return $this->post('/api/auth/reenviar-verificacao', ['email' => $email]);
    }

    public function getAuthenticated(string $path, string $jwtToken): ApiResponseDto
    {
        return $this->get($path, $jwtToken);
    }

    public function postAuthenticated(string $path, array $body, string $jwtToken): ApiResponseDto
    {
        return $this->post($path, $body, $jwtToken);
    }

    private function post(string $path, array $body, ?string $jwtToken = null): ApiResponseDto
    {
        $headers = [
            'Content-Type' => 'application/json',
            'Accept' => 'application/json',
        ];

        if ($jwtToken !== null) {
            $headers['Authorization'] = 'Bearer ' . $jwtToken;
        }

        try {
            $response = $this->client->request('POST', $this->baseUrl . $path, [
                'headers' => $headers,
                'json' => $body,
            ]);

            return $this->buildResponse($response);
        } catch (TransportExceptionInterface $e) {
            return new ApiResponseDto(
                success: false,
                statusCode: 0,
                data: [],
                error: 'Erro ao conectar com o servidor. Tente novamente.',
                apiErrorCode: 'TRANSPORT_ERROR',
                fieldErrors: [],
            );
        }
    }

    private function get(string $path, ?string $jwtToken = null): ApiResponseDto
    {
        $headers = ['Accept' => 'application/json'];

        if ($jwtToken !== null) {
            $headers['Authorization'] = 'Bearer ' . $jwtToken;
        }

        try {
            $response = $this->client->request('GET', $this->baseUrl . $path, [
                'headers' => $headers,
            ]);

            return $this->buildResponse($response);
        } catch (TransportExceptionInterface $e) {
            return new ApiResponseDto(
                success: false,
                statusCode: 0,
                data: [],
                error: 'Erro ao conectar com o servidor. Tente novamente.',
                apiErrorCode: 'TRANSPORT_ERROR',
                fieldErrors: [],
            );
        }
    }

    private function buildResponse($response): ApiResponseDto
    {
        $statusCode = $response->getStatusCode();
        $data = $response->toArray(false);

        if ($statusCode >= 400) {
            return new ApiResponseDto(
                success: false,
                statusCode: $statusCode,
                data: [],
                error: $data['message'] ?? 'Ocorreu um erro inesperado.',
                apiErrorCode: $data['code'] ?? 'UNKNOWN',
                fieldErrors: $data['fieldErrors'] ?? [],
            );
        }

        return new ApiResponseDto(
            success: true,
            statusCode: $statusCode,
            data: $data,
            error: null,
            apiErrorCode: null,
            fieldErrors: [],
        );
    }
}
