<?php

namespace App\Controller\Home;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

final class HomeController extends AbstractController
{

    #[Route('/home', name: 'app_home', methods: ['GET'])]
    public function index(): Response
    {
        return $this->render('home/index.html.twig');
    }

    #[Route('/app', name: 'app_pos_login', methods: ['GET'])]
    public function getApp(): Response
    {
        return $this->render('home/telaPosLogin.html.twig');
    }

}
