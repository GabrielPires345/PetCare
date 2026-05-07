<?php

namespace App\Controller\Auth;

use App\Dto\Request\Auth\LoginRequestDto;
use App\Exception\ValidationException;
use App\Service\Auth\AuthService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

final class LoginController extends AbstractController
{
    public function __construct(
        private AuthService $authService,
    ) {}

    #[Route('/login/form', name: 'app_login_form', methods: ['GET'])]
    public function loginForm(): Response
    {
        return $this->render('login/index.html.twig');
    }

    #[Route('/login', name: 'app_login', methods: ['POST'])]
    public function login(Request $request): Response
    {
        $submittedToken = $request->request->get('_csrf_token', '');
        if (!$this->isCsrfTokenValid('authenticate', $submittedToken)) {
            $this->addFlash('error', 'Token de segurança inválido. Tente novamente.');
            return $this->redirectToRoute('app_login_form');
        }

        try {
            $dto = new LoginRequestDto($request->request->all());
            $this->authService->login($dto);
        } catch (\Throwable $e) {
            $this->addFlash('error', $e->getMessage());
            return $this->redirectToRoute('app_login_form');
        }

        return $this->redirectToRoute('app_pos_login');
    }
}
