package com.petcare.exception;

import lombok.Getter;

/**
 * Exceção base para regras de negócio violadas
 */
@Getter
public class NegocioException extends RuntimeException {

    private final ErrorCode errorCode;

    public NegocioException(String mensagem) {
        super(mensagem);
        this.errorCode = ErrorCode.ERRO_INTERNO;
    }

    public NegocioException(ErrorCode errorCode, String mensagem) {
        super(mensagem);
        this.errorCode = errorCode;
    }
}