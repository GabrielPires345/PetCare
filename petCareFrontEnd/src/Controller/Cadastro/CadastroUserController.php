<?php

namespace App\Controller\Cadastro;

use App\Services\UserServices;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;
use Symfony\Contracts\HttpClient\HttpClientInterface;

final class CadastroUserController extends AbstractController
{
    public function __construct(
        private UserServices        $userServices,
        private HttpClientInterface $client
    )
    {
    }

    #[Route('/cadastro/form', name: 'app_cadastro_user_form', methods: ['GET'])]
    public function cadastroUserForm(): Response
    {
        return $this->render('cadastroUser/index.html.twig');
    }

    #[Route('/cadastro', name: 'app_cadastro', methods: ['POST'])]
    public function loginForm(): Response
    {
        return $this->render('cadastroUser/telaPosLogin.html.twig.twig');
    }

}
