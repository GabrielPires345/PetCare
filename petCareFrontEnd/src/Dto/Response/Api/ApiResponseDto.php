<?php

namespace App\Dto\Response\Api;

class ApiResponseDto
{
    public function __construct(
        private bool   $success,
        private int    $statusCode,
        private array  $data,
        private ?string $error,
        private ?string $apiErrorCode,
        private array  $fieldErrors,
    ) {}

    public function isSuccess(): bool
    {
        return $this->success;
    }

    public function getStatusCode(): int
    {
        return $this->statusCode;
    }

    public function getData(): array
    {
        return $this->data;
    }

    public function getError(): ?string
    {
        return $this->error;
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
