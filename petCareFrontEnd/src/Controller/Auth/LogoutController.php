<?php

namespace App\Controller\Auth;

use App\Service\Auth\AuthService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

final class LogoutController extends AbstractController
{
    public function __construct(
        private AuthService $authService,
    ) {}

    #[Route('/logout', name: 'app_logout', methods: ['POST'])]
    public function logout(Request $request): Response
    {
        if (!$this->isCsrfTokenValid('logout', $request->request->get('_csrf_token'))) {
            return $this->redirectToRoute('app_pos_login');
        }

        $this->authService->logout();

        return $this->redirectToRoute('app_home');
    }
}
