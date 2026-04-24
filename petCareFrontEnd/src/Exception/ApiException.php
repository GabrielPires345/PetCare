<?php

namespace App\Exception;

class ApiException extends \RuntimeException
{
    public function __construct(
        string $message,
        private ?string $apiErrorCode = null,
        private array $fieldErrors = [],
        int $code = 0,
        ?\Throwable $previous = null,
    ) {
        parent::__construct($message, $code, $previous);
    }

    public function getApiErrorCode(): ?string
    {
        return $this->apiErrorCode;
    }

    public function getFieldErrors(): array
    {
        return $this->fieldErrors;
    }

    public function hasFieldErrors(): bool
    {
        return !empty($this->fieldErrors);
    }
}
