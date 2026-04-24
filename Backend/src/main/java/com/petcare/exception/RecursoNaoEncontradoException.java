package com.petcare.exception;

import lombok.Getter;

@Getter
public class RecursoNaoEncontradoException extends RuntimeException {

    private final ErrorCode errorCode;

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
        this.errorCode = ErrorCode.RECURSO_NAO_ENCONTRADO;
    }

    public RecursoNaoEncontradoException(String resourceName, Object identifier) {
        super(String.format("%s não encontrado com identificador: %s", resourceName, identifier));
        this.errorCode = ErrorCode.RECURSO_NAO_ENCONTRADO;
    }

    public RecursoNaoEncontradoException(ErrorCode errorCode, String mensagem) {
        super(mensagem);
        this.errorCode = errorCode;
    }
}