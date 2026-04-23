package com.petcare.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 400 Bad Request
    VALIDACAO_INVALIDA("VALIDACAO_INVALIDA", "Dados de entrada inválidos", HttpStatus.BAD_REQUEST),
    REQUISICAO_MALFORMADA("REQUISICAO_MALFORMADA", "JSON malformado ou formato inválido", HttpStatus.BAD_REQUEST),
    PARAMETRO_FALTANDO("PARAMETRO_FALTANDO", "Parâmetro obrigatório não informado", HttpStatus.BAD_REQUEST),
    SENHAS_NAO_CONFEREM("SENHAS_NAO_CONFEREM", "As senhas não conferem", HttpStatus.BAD_REQUEST),

    // 401 Unauthorized
    CREDENCIAIS_INVALIDAS("CREDENCIAIS_INVALIDAS", "Credenciais inválidas", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALIDO("TOKEN_INVALIDO", "Token JWT inválido ou expirado", HttpStatus.UNAUTHORIZED),
    NAO_AUTENTICADO("NAO_AUTENTICADO", "Autenticação necessária", HttpStatus.UNAUTHORIZED),
    EMAIL_NAO_VERIFICADO("EMAIL_NAO_VERIFICADO", "Email ainda não verificado", HttpStatus.UNAUTHORIZED),

    // 403 Forbidden
    ACESSO_NEGADO("ACESSO_NEGADO", "Você não tem permissão para acessar este recurso", HttpStatus.FORBIDDEN),

    // 404 Not Found
    RECURSO_NAO_ENCONTRADO("RECURSO_NAO_ENCONTRADO", "Recurso não encontrado", HttpStatus.NOT_FOUND),
    ENDPOINT_NAO_ENCONTRADO("ENDPOINT_NAO_ENCONTRADO", "Endpoint não encontrado", HttpStatus.NOT_FOUND),

    // 409 Conflict
    RECURSO_DUPLICADO("RECURSO_DUPLICADO", "Recurso já existe", HttpStatus.CONFLICT),
    VIOLACAO_INTEGRIDADE("VIOLACAO_INTEGRIDADE", "Violação de integridade de dados", HttpStatus.CONFLICT),

    // 500 Internal Server Error
    ERRO_INTERNO("ERRO_INTERNO", "Erro interno do servidor", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus status;

    ErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}