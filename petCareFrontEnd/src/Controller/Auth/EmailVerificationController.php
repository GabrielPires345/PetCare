<?php

namespace App\Controller\Auth;

use App\Service\Auth\AuthService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

final class EmailVerificationController extends AbstractController
{
    public function __construct(
        private AuthService $authService,
    ) {}

    #[Route('/verificar-email', name: 'app_verificar_email', methods: ['GET'])]
    public function verifyEmail(Request $request): Response
    {
        $token = $request->query->get('token', '');

        if (empty($token)) {
            $this->addFlash('error', 'Token de verificação não informado.');
            return $this->redirectToRoute('app_login_form');
        }

        try {
            $this->authService->verifyEmail($token);
        } catch (\Throwable $e) {
            $this->addFlash('error', $e->getMessage());
            return $this->redirectToRoute('app_login_form');
        }

        $this->addFlash('success', 'Email verificado com sucesso! Você já pode fazer login.');
        return $this->redirectToRoute('app_login_form');
    }
}
