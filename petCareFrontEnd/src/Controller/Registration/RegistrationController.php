<?php

namespace App\Controller\Registration;

use App\Dto\Request\Registration\RegistrationType;
use App\Exception\ValidationException;
use App\Service\Registration\RegistrationService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

final class RegistrationController extends AbstractController
{
    public function __construct(
        private RegistrationService $registrationService,
    ) {}

    #[Route('/cadastro/form', name: 'app_cadastro_user_form', methods: ['GET'])]
    public function registrationForm(): Response
    {
        return $this->render('cadastroUser/index.html.twig');
    }

    #[Route('/cadastro', name: 'app_cadastro', methods: ['POST'])]
    public function register(Request $request): Response
    {
        $submittedToken = $request->request->get('_csrf_token', '');
        if (!$this->isCsrfTokenValid('registration', $submittedToken)) {
            throw new ValidationException('Token CSRF inválido.');
        }

        $data = $request->request->all();
        $nivelAcesso = $data['nivelAcesso'] ?? '';

        $tipo = RegistrationType::tryFrom($nivelAcesso);
        if ($tipo === null) {
            $this->addFlash('error', 'Selecione o tipo de conta.');
            return $this->redirectToRoute('app_cadastro_user_form');
        }

        $this->registrationService->register($tipo, $data);

        $this->addFlash('success', 'Registro realizado com sucesso! Verifique seu email para ativar sua conta.');
        return $this->redirectToRoute('app_login_form');
    }
}
