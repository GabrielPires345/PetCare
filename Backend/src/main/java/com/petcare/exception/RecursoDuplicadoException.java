package com.petcare.exception;

import lombok.Getter;

@Getter
public class RecursoDuplicadoException extends RuntimeException {

    private final ErrorCode errorCode;

    public RecursoDuplicadoException(String mensagem) {
        super(mensagem);
        this.errorCode = ErrorCode.RECURSO_DUPLICADO;
    }

    public RecursoDuplicadoException(String resourceName, String fieldName, Object value) {
        super(String.format("%s já existe com %s: %s", resourceName, fieldName, value));
        this.errorCode = ErrorCode.RECURSO_DUPLICADO;
    }

    public RecursoDuplicadoException(ErrorCode errorCode, String mensagem) {
        super(mensagem);
        this.errorCode = errorCode;
    }
}