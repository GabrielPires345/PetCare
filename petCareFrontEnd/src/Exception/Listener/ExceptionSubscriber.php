<?php

namespace App\Exception\Listener;

use App\Exception\ApiException;
use App\Exception\AuthenticationException;
use App\Exception\ValidationException;
use Psr\Log\LoggerInterface;
use Symfony\Component\EventDispatcher\EventSubscriberInterface;
use Symfony\Component\HttpFoundation\RedirectResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpKernel\Event\ExceptionEvent;
use Symfony\Component\HttpKernel\KernelEvents;
use Symfony\Component\Routing\Generator\UrlGeneratorInterface;

class ExceptionSubscriber implements EventSubscriberInterface
{
    private const ERROR_MESSAGES = [
        'CREDENCIAIS_INVALIDAS' => 'Email ou senha incorretos.',
        'EMAIL_NAO_VERIFICADO' => 'Email ainda não verificado. Verifique sua caixa de entrada.',
        'SENHAS_NAO_CONFEREM' => 'As senhas não conferem.',
        'RECURSO_DUPLICADO' => 'Este email já está cadastrado.',
        'VALIDACAO_INVALIDA' => 'Dados inválidos. Verifique os campos preenchidos.',
        'PARAMETRO_FALTANDO' => 'Campo obrigatório não preenchido.',
        'RECURSO_NAO_ENCONTRADO' => 'Recurso não encontrado.',
        'TRANSPORT_ERROR' => 'Erro ao conectar com o servidor. Tente novamente.',
    ];

    public function __construct(
        private UrlGeneratorInterface $urlGenerator,
        private LoggerInterface $logger,
    ) {}

    public static function getSubscribedEvents(): array
    {
        return [
            KernelEvents::EXCEPTION => 'onKernelException',
        ];
    }

    public function onKernelException(ExceptionEvent $event): void
    {
        $exception = $event->getThrowable();

        if (!($exception instanceof ApiException)) {
            return;
        }

        $request = $event->getRequest();
        $session = $request->hasSession() ? $request->getSession() : null;

        $userMessage = $this->translateErrorMessage($exception);

        if ($session !== null) {
            $session->getFlashBag()->add('error', $userMessage);

            if ($exception->hasFieldErrors()) {
                $messages = array_map(
                    fn($e) => ($e['field'] ?? '') . ': ' . ($e['message'] ?? ''),
                    $exception->getFieldErrors()
                );
                $session->getFlashBag()->add('fieldErrors', $messages);
            }
        }

        $redirectRoute = match (true) {
            $exception instanceof AuthenticationException => 'app_login_form',
            $exception instanceof ValidationException => $this->guessValidationRedirectRoute($request),
            default => 'app_home',
        };

        $event->setResponse(new RedirectResponse(
            $this->urlGenerator->generate($redirectRoute)
        ));

        $this->logger->warning('App exception: ' . $exception->getMessage(), [
            'apiErrorCode' => $exception->getApiErrorCode(),
            'route' => $request->attributes->get('_route'),
        ]);
    }

    private function guessValidationRedirectRoute(Request $request): string
    {
        $referer = $request->headers->get('referer', '');
        if (str_contains($referer, '/login')) {
            return 'app_login_form';
        }
        if (str_contains($referer, '/cadastro')) {
            return 'app_cadastro_user_form';
        }
        return 'app_cadastro_user_form';
    }

    private function translateErrorMessage(ApiException $e): string
    {
        $code = $e->getApiErrorCode();
        if ($code !== null && isset(self::ERROR_MESSAGES[$code])) {
            return self::ERROR_MESSAGES[$code];
        }
        return $e->getMessage();
    }
}
