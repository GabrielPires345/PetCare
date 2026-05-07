<?php

namespace App\Controller\Home;

use App\Service\Api\BackendApiService;
use App\Service\Auth\AuthService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

class HomeController extends AbstractController
{
    public function __construct(
        private AuthService $authService,
        private BackendApiService $api,
    ) {}

    #[Route('/', name: 'app_home')]
    public function index(): Response
    {
        return $this->render('home/index.html.twig');
    }

    #[Route('/app', name: 'app_pos_login')]
    public function posLogin(): Response
    {
        $jwt = $this->authService->getToken();
        $tipoPerfil = $this->authService->getTipoPerfil();
        $perfilId = $this->authService->getPerfilId();

        if (!$jwt || !$perfilId) {
            return $this->redirectToRoute('app_login_form');
        }

        $pets = [];
        $agendamentos = [];

        if ($tipoPerfil === 'CLIENTE') {
            $petsResult = $this->api->getPets($perfilId, $jwt);
            if ($petsResult->isSuccess()) {
                $pets = $petsResult->getData();
            }

            $agResult = $this->api->getAgendamentos($perfilId, $jwt);
            if ($agResult->isSuccess()) {
                $agendamentos = $agResult->getData();
            }
        }

        return $this->render('home/telaPosLogin.html.twig', [
            'pets' => $pets,
            'agendamentos' => $agendamentos,
            'totalPets' => count($pets),
            'totalAgendamentos' => count($agendamentos),
        ]);
    }
}
