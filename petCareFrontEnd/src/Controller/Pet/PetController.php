<?php

namespace App\Controller\Pet;

use App\Service\Api\BackendApiService;
use App\Service\Auth\AuthService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

class PetController extends AbstractController
{
    public function __construct(
        private AuthService $authService,
        private BackendApiService $api,
    ) {}

    #[Route('/app/pets', name: 'app_pets')]
    public function index(): Response
    {
        $jwt = $this->authService->getToken();
        $perfilId = $this->authService->getPerfilId();

        if (!$jwt || !$perfilId) {
            return $this->redirectToRoute('app_login_form');
        }

        $pets = [];
        $result = $this->api->getPets($perfilId, $jwt);
        if ($result->isSuccess()) {
            $pets = $result->getData();
        }

        return $this->render('pets/index.html.twig', [
            'pets' => $pets,
        ]);
    }

    #[Route('/app/pets/novo', name: 'app_pet_novo', methods: ['GET'])]
    public function novo(): Response
    {
        if (!$this->authService->getToken() || !$this->authService->getPerfilId()) {
            return $this->redirectToRoute('app_login_form');
        }

        return $this->render('pets/form.html.twig');
    }

    #[Route('/app/pets/novo', name: 'app_pet_criar', methods: ['POST'])]
    public function criar(Request $request): Response
    {
        if (!$this->isCsrfTokenValid('pet_create', $request->request->get('_csrf_token'))) {
            $this->addFlash('error', 'Token de segurança inválido. Tente novamente.');
            return $this->redirectToRoute('app_pet_novo');
        }

        $jwt = $this->authService->getToken();
        $perfilId = $this->authService->getPerfilId();

        if (!$jwt || !$perfilId) {
            $this->addFlash('error', 'Sessão expirada. Faça login novamente.');
            return $this->redirectToRoute('app_login_form');
        }

        $peso = $request->request->get('peso');
        $data = [
            'nome' => $request->request->get('nome'),
            'especie' => $request->request->get('especie'),
            'sexo' => $request->request->get('sexo'),
            'peso' => $peso !== null && $peso !== '' ? (float) $peso : null,
            'dataNascimento' => $request->request->get('dataNascimento'),
            'castrado' => $request->request->has('castrado'),
        ];

        $result = $this->api->addPet($perfilId, $data, $jwt);

        if (!$result->isSuccess()) {
            $this->addFlash('error', $result->getError() ?? 'Erro ao cadastrar pet.');
            if ($result->getFieldErrors()) {
                $this->addFlash('fieldErrors', $result->getFieldErrors());
            }
            return $this->redirectToRoute('app_pet_novo');
        }

        $this->addFlash('success', 'Pet cadastrado com sucesso!');
        return $this->redirectToRoute('app_pets');
    }

    #[Route('/app/pets/{petId}/remover', name: 'app_pet_remover', methods: ['POST'])]
    public function remover(Request $request, string $petId): Response
    {
        if (!$this->isCsrfTokenValid('pet_remove', $request->request->get('_csrf_token'))) {
            $this->addFlash('error', 'Token de segurança inválido. Tente novamente.');
            return $this->redirectToRoute('app_pets');
        }

        $jwt = $this->authService->getToken();

        if (!$jwt) {
            $this->addFlash('error', 'Sessão expirada. Faça login novamente.');
            return $this->redirectToRoute('app_login_form');
        }

        $result = $this->api->deletePet($petId, $jwt);

        if (!$result->isSuccess()) {
            $this->addFlash('error', $result->getError() ?? 'Erro ao remover pet.');
        } else {
            $this->addFlash('success', 'Pet removido com sucesso!');
        }

        return $this->redirectToRoute('app_pets');
    }
}
