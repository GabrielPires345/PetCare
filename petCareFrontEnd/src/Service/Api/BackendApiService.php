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

    public function putAuthenticated(string $path, array $body, string $jwtToken): ApiResponseDto
    {
        return $this->put($path, $body, $jwtToken);
    }

    public function deleteAuthenticated(string $path, string $jwtToken): ApiResponseDto
    {
        return $this->delete($path, $jwtToken);
    }

    // --- Pets ---

    public function getPets(string $clienteId, string $jwt): ApiResponseDto
    {
        return $this->getAuthenticated('/api/pets/cliente/' . $clienteId, $jwt);
    }

    public function addPet(string $clienteId, array $data, string $jwt): ApiResponseDto
    {
        return $this->postAuthenticated('/api/pets/cliente/' . $clienteId, $data, $jwt);
    }

    public function deletePet(string $petId, string $jwt): ApiResponseDto
    {
        return $this->deleteAuthenticated('/api/pets/' . $petId, $jwt);
    }

    // --- Agendamentos ---

    public function getAgendamentos(string $clienteId, string $jwt): ApiResponseDto
    {
        return $this->getAuthenticated('/api/agendamentos/cliente/' . $clienteId, $jwt);
    }

    public function createAgendamento(array $data, string $jwt): ApiResponseDto
    {
        return $this->postAuthenticated('/api/agendamentos', $data, $jwt);
    }

    public function cancelAgendamento(string $id, string $jwt): ApiResponseDto
    {
        return $this->deleteAuthenticated('/api/agendamentos/' . $id, $jwt);
    }

    // --- Clinicas / Servicos / Especialidades ---

    public function getClinicas(string $jwt): ApiResponseDto
    {
        return $this->getAuthenticated('/api/clinicas', $jwt);
    }

    public function getServicos(): ApiResponseDto
    {
        return $this->get('/api/servicos');
    }

    public function getEspecialidades(string $jwt): ApiResponseDto
    {
        return $this->getAuthenticated('/api/especialidades', $jwt);
    }

    // --- Cliente ---

    public function getCliente(string $id, string $jwt): ApiResponseDto
    {
        return $this->getAuthenticated('/api/clientes/' . $id, $jwt);
    }

    public function updateCliente(string $id, array $data, string $jwt): ApiResponseDto
    {
        return $this->putAuthenticated('/api/clientes/' . $id, $data, $jwt);
    }

    // --- Clinica ---

    public function getClinica(string $id, string $jwt): ApiResponseDto
    {
        return $this->getAuthenticated('/api/clinicas/' . $id, $jwt);
    }

    public function updateClinica(string $id, array $data, string $jwt): ApiResponseDto
    {
        return $this->putAuthenticated('/api/clinicas/' . $id, $data, $jwt);
    }

    // --- Veterinario ---

    public function getVeterinario(string $id, string $jwt): ApiResponseDto
    {
        return $this->getAuthenticated('/api/veterinarios/' . $id, $jwt);
    }

    public function updateVeterinario(string $id, array $data, string $jwt): ApiResponseDto
    {
        return $this->putAuthenticated('/api/veterinarios/' . $id, $data, $jwt);
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

    private function put(string $path, array $body, ?string $jwtToken = null): ApiResponseDto
    {
        $headers = [
            'Content-Type' => 'application/json',
            'Accept' => 'application/json',
        ];

        if ($jwtToken !== null) {
            $headers['Authorization'] = 'Bearer ' . $jwtToken;
        }

        try {
            $response = $this->client->request('PUT', $this->baseUrl . $path, [
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

    private function delete(string $path, ?string $jwtToken = null): ApiResponseDto
    {
        $headers = ['Accept' => 'application/json'];

        if ($jwtToken !== null) {
            $headers['Authorization'] = 'Bearer ' . $jwtToken;
        }

        try {
            $response = $this->client->request('DELETE', $this->baseUrl . $path, [
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

        if ($statusCode === 204) {
            return new ApiResponseDto(
                success: true,
                statusCode: $statusCode,
                data: [],
                error: null,
                apiErrorCode: null,
                fieldErrors: [],
            );
        }

        try {
            $data = $response->toArray(false);
        } catch (\Throwable) {
            $data = [];
        }

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
