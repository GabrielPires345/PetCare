<?php

namespace App\Controller\Perfil;

use App\Service\Api\BackendApiService;
use App\Service\Auth\AuthService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

class PerfilController extends AbstractController
{
    public function __construct(
        private AuthService $authService,
        private BackendApiService $api,
    ) {}

    #[Route('/app/perfil', name: 'app_perfil')]
    public function index(): Response
    {
        $jwt = $this->authService->getToken();
        $perfilId = $this->authService->getPerfilId();
        $tipoPerfil = $this->authService->getTipoPerfil();
        $user = $this->authService->getUser();

        if (!$jwt || !$perfilId || !$tipoPerfil) {
            return $this->redirectToRoute('app_login_form');
        }

        $perfil = null;
        $result = $this->fetchPerfil($tipoPerfil, $perfilId, $jwt);
        if ($result->isSuccess()) {
            $perfil = $result->getData();
        }

        return $this->render('perfil/index.html.twig', [
            'user' => $user,
            'perfil' => $perfil,
            'tipoPerfil' => $tipoPerfil,
        ]);
    }

    #[Route('/app/perfil/editar', name: 'app_perfil_editar', methods: ['GET'])]
    public function editar(): Response
    {
        $jwt = $this->authService->getToken();
        $perfilId = $this->authService->getPerfilId();
        $tipoPerfil = $this->authService->getTipoPerfil();
        $user = $this->authService->getUser();

        if (!$jwt || !$perfilId || !$tipoPerfil) {
            return $this->redirectToRoute('app_login_form');
        }

        $perfil = null;
        $result = $this->fetchPerfil($tipoPerfil, $perfilId, $jwt);
        if ($result->isSuccess()) {
            $perfil = $result->getData();
        }

        return $this->render('perfil/editar.html.twig', [
            'user' => $user,
            'perfil' => $perfil,
            'tipoPerfil' => $tipoPerfil,
        ]);
    }

    #[Route('/app/perfil/editar', name: 'app_perfil_salvar', methods: ['POST'])]
    public function salvar(Request $request): Response
    {
        if (!$this->isCsrfTokenValid('perfil_edit', $request->request->get('_csrf_token'))) {
            $this->addFlash('error', 'Token de segurança inválido. Tente novamente.');
            return $this->redirectToRoute('app_perfil_editar');
        }

        $jwt = $this->authService->getToken();
        $perfilId = $this->authService->getPerfilId();
        $tipoPerfil = $this->authService->getTipoPerfil();

        if (!$jwt || !$perfilId || !$tipoPerfil) {
            return $this->redirectToRoute('app_login_form');
        }

        $data = $this->extractUpdateData($tipoPerfil, $request);
        $result = $this->updatePerfil($tipoPerfil, $perfilId, $data, $jwt);

        if (!$result->isSuccess()) {
            $this->addFlash('error', $result->getError() ?? 'Erro ao atualizar perfil.');
            return $this->redirectToRoute('app_perfil_editar');
        }

        $this->addFlash('success', 'Perfil atualizado com sucesso!');
        return $this->redirectToRoute('app_perfil');
    }

    private function fetchPerfil(string $tipoPerfil, string $id, string $jwt): \App\Dto\Response\Api\ApiResponseDto
    {
        return match ($tipoPerfil) {
            'CLIENTE' => $this->api->getCliente($id, $jwt),
            'CLINICA' => $this->api->getClinica($id, $jwt),
            'VETERINARIO' => $this->api->getVeterinario($id, $jwt),
            default => new \App\Dto\Response\Api\ApiResponseDto(
                success: false, statusCode: 0, data: [], error: 'Tipo de perfil desconhecido.',
                apiErrorCode: 'UNKNOWN_TYPE', fieldErrors: [],
            ),
        };
    }

    private function updatePerfil(string $tipoPerfil, string $id, array $data, string $jwt): \App\Dto\Response\Api\ApiResponseDto
    {
        return match ($tipoPerfil) {
            'CLIENTE' => $this->api->updateCliente($id, $data, $jwt),
            'CLINICA' => $this->api->updateClinica($id, $data, $jwt),
            'VETERINARIO' => $this->api->updateVeterinario($id, $data, $jwt),
            default => new \App\Dto\Response\Api\ApiResponseDto(
                success: false, statusCode: 0, data: [], error: 'Tipo de perfil desconhecido.',
                apiErrorCode: 'UNKNOWN_TYPE', fieldErrors: [],
            ),
        };
    }

    private function extractUpdateData(string $tipoPerfil, Request $request): array
    {
        return match ($tipoPerfil) {
            'CLIENTE' => [
                'nomeCompleto' => $request->request->get('nomeCompleto'),
                'cpf' => $request->request->get('cpf'),
                'dataNascimento' => $request->request->get('dataNascimento'),
            ],
            'CLINICA' => [
                'nomeClinica' => $request->request->get('nomeClinica'),
                'razaoSocial' => $request->request->get('razaoSocial'),
                'cnpj' => $request->request->get('cnpj'),
            ],
            'VETERINARIO' => [
                'nome' => $request->request->get('nome'),
                'crmv' => $request->request->get('crmv'),
            ],
            default => [],
        };
    }
}
