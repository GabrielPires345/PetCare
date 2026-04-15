<?php

namespace App\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

final class HomeController extends AbstractController
{
    #[Route('/home', name: 'app_home', methods: ['GET'])]
    public function index(): Response
    {
        return $this->render('landing_page/index.html.twig');
    }

    #[Route('/login', name: 'app_login', methods: ['GET'])]
    public function getLogin(): Response
    {
        return $this->render('home/index.html.twig');
    }

    #[Route('/cadastro', name: 'app_cadastro', methods: ['POST'])]
    public function getCadastro(): Response
    {
        return $this->render('home/index.html.twig');
    }

}
