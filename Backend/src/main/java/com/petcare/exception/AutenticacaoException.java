package com.petcare.exception;

import lombok.Getter;

/**
 * Exceção para erros de autenticação
 */
@Getter
public class AutenticacaoException extends RuntimeException {

    private final ErrorCode errorCode;

    public AutenticacaoException(String mensagem) {
        super(mensagem);
        this.errorCode = ErrorCode.CREDENCIAIS_INVALIDAS;
    }

    public AutenticacaoException(ErrorCode errorCode, String mensagem) {
        super(mensagem);
        this.errorCode = errorCode;
    }
}