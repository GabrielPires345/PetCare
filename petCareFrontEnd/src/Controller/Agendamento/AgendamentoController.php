<?php

namespace App\Controller\Agendamento;

use App\Service\Api\BackendApiService;
use App\Service\Auth\AuthService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

class AgendamentoController extends AbstractController
{
    public function __construct(
        private AuthService $authService,
        private BackendApiService $api,
    ) {}

    #[Route('/app/agendamentos', name: 'app_agendamentos')]
    public function index(): Response
    {
        $jwt = $this->authService->getToken();
        $perfilId = $this->authService->getPerfilId();

        if (!$jwt || !$perfilId) {
            return $this->redirectToRoute('app_login_form');
        }

        $agendamentos = [];
        $result = $this->api->getAgendamentos($perfilId, $jwt);
        if ($result->isSuccess()) {
            $agendamentos = $result->getData();
        }

        return $this->render('agendamentos/index.html.twig', [
            'agendamentos' => $agendamentos,
        ]);
    }

    #[Route('/app/agendamentos/novo', name: 'app_agendamento_novo', methods: ['GET'])]
    public function novo(): Response
    {
        $jwt = $this->authService->getToken();
        $perfilId = $this->authService->getPerfilId();

        if (!$jwt || !$perfilId) {
            return $this->redirectToRoute('app_login_form');
        }

        $pets = [];
        $clinicas = [];
        $servicos = [];

        $petsResult = $this->api->getPets($perfilId, $jwt);
        if ($petsResult->isSuccess()) {
            $pets = $petsResult->getData();
        }

        $clinicasResult = $this->api->getClinicas($jwt);
        if ($clinicasResult->isSuccess()) {
            $clinicas = $clinicasResult->getData();
        }

        $servicosResult = $this->api->getServicos();
        if ($servicosResult->isSuccess()) {
            $servicos = $servicosResult->getData();
        }

        return $this->render('agendamentos/form.html.twig', [
            'pets' => $pets,
            'clinicas' => $clinicas,
            'servicos' => $servicos,
        ]);
    }

    #[Route('/app/agendamentos/novo', name: 'app_agendamento_criar', methods: ['POST'])]
    public function criar(Request $request): Response
    {
        if (!$this->isCsrfTokenValid('agendamento_create', $request->request->get('_csrf_token'))) {
            $this->addFlash('error', 'Token de segurança inválido. Tente novamente.');
            return $this->redirectToRoute('app_agendamento_novo');
        }

        $jwt = $this->authService->getToken();
        $perfilId = $this->authService->getPerfilId();

        if (!$jwt || !$perfilId) {
            $this->addFlash('error', 'Sessão expirada. Faça login novamente.');
            return $this->redirectToRoute('app_login_form');
        }

        $data = [
            'clienteId' => $perfilId,
            'petId' => $request->request->get('petId'),
            'clinicaId' => $request->request->get('clinicaId'),
            'servicoId' => $request->request->get('servicoId'),
            'dataHora' => $request->request->get('dataHora'),
        ];

        $result = $this->api->createAgendamento($data, $jwt);

        if (!$result->isSuccess()) {
            $this->addFlash('error', $result->getError() ?? 'Erro ao criar agendamento.');
            if ($result->getFieldErrors()) {
                $this->addFlash('fieldErrors', $result->getFieldErrors());
            }
            return $this->redirectToRoute('app_agendamento_novo');
        }

        $this->addFlash('success', 'Agendamento criado com sucesso!');
        return $this->redirectToRoute('app_agendamentos');
    }

    #[Route('/app/agendamentos/{id}/cancelar', name: 'app_agendamento_cancelar', methods: ['POST'])]
    public function cancelar(Request $request, string $id): Response
    {
        if (!$this->isCsrfTokenValid('agendamento_cancel', $request->request->get('_csrf_token'))) {
            $this->addFlash('error', 'Token de segurança inválido. Tente novamente.');
            return $this->redirectToRoute('app_agendamentos');
        }

        $jwt = $this->authService->getToken();

        if (!$jwt) {
            $this->addFlash('error', 'Sessão expirada. Faça login novamente.');
            return $this->redirectToRoute('app_login_form');
        }

        $result = $this->api->cancelAgendamento($id, $jwt);

        if (!$result->isSuccess()) {
            $this->addFlash('error', $result->getError() ?? 'Erro ao cancelar agendamento.');
        } else {
            $this->addFlash('success', 'Agendamento cancelado com sucesso!');
        }

        return $this->redirectToRoute('app_agendamentos');
    }
}
