<?php

namespace App\Controller\Login;

use App\Dto\Cadastro\ClienteCadastroDto;
use App\Services\UserServices;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;
use Symfony\Contracts\HttpClient\HttpClientInterface;

final class LoginController extends AbstractController
{

    public function __construct(
        private UserServices        $userServices,
        private HttpClientInterface $client
    )
    {
    }

    #[Route('/login/form', name: 'app_login_form', methods: ['GET'])]
    public function loginForm(): Response
    {
        return $this->render('login/index.html.twig');
    }

    #[Route('/login', name: 'app_login', methods: ['POST'])]
    public function login(Request $request): Response
    {
        return $this->handleUserRequest($request);
    }

    private function handleUserRequest(Request $request): Response
    {
        $dto = new ClienteCadastroDto($request->request->all());

        if (!$this->userServices->validateForm($dto)) {
            return $this->redirectToRoute('app_login');
        }

        try {
            $response = $this->client->request('POST', 'register', [
                'headers' => [
                    'Content-Type' => 'application/json',
                    'Accept' => 'application/json',
                ],
                'form_params' => $this->parseData($dto)
            ]);

            $data = $response->toArray();

        } catch (\Throwable $e) {
            $data = [
                'error' => 'Erro ao comunicar com a API'
            ];
        }

        return $this->render('login/telaPosLogin.html.twig.twig', [
            'response' => $data
        ]);
    }

    private function parseData(ClienteCadastroDto $dto): array
    {
        return [
            'email' => $dto->getEmail(),
            'password' => $dto->getPassword(),
        ];
    }
}
