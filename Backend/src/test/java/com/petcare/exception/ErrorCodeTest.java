package com.petcare.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

class ErrorCodeTest {

    @Test
    @DisplayName("Deve conter código de erro VALIDACAO_INVALIDA")
    void deveConterCodigoValidacaoInvalida() {
        ErrorCode errorCode = ErrorCode.VALIDACAO_INVALIDA;

        assertEquals("VALIDACAO_INVALIDA", errorCode.getCode());
        assertEquals("Dados de entrada inválidos", errorCode.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, errorCode.getStatus());
    }

    @Test
    @DisplayName("Deve conter código de erro CREDENCIAIS_INVALIDAS")
    void deveConterCodigoCredenciaisValidas() {
        ErrorCode errorCode = ErrorCode.CREDENCIAIS_INVALIDAS;

        assertEquals("CREDENCIAIS_INVALIDAS", errorCode.getCode());
        assertEquals(HttpStatus.UNAUTHORIZED, errorCode.getStatus());
    }

    @Test
    @DisplayName("Deve conter código de erro ACESSO_NEGADO")
    void deveConterCodigoAcessoNegado() {
        ErrorCode errorCode = ErrorCode.ACESSO_NEGADO;

        assertEquals("ACESSO_NEGADO", errorCode.getCode());
        assertEquals(HttpStatus.FORBIDDEN, errorCode.getStatus());
    }

    @Test
    @DisplayName("Deve conter código de erro RECURSO_NAO_ENCONTRADO")
    void deveConterCodigoRecursoNaoEncotrado() {
        ErrorCode errorCode = ErrorCode.RECURSO_NAO_ENCONTRADO;

        assertEquals("RECURSO_NAO_ENCONTRADO", errorCode.getCode());
        assertEquals(HttpStatus.NOT_FOUND, errorCode.getStatus());
    }

    @Test
    @DisplayName("Deve conter código de erro RECURSO_DUPLICADO")
    void deveConterCodigoRecursoDuplicado() {
        ErrorCode errorCode = ErrorCode.RECURSO_DUPLICADO;

        assertEquals("RECURSO_DUPLICADO", errorCode.getCode());
        assertEquals(HttpStatus.CONFLICT, errorCode.getStatus());
    }

    @Test
    @DisplayName("Deve conter código de erro ERRO_INTERNO")
    void deveConterCodigoErroInterno() {
        ErrorCode errorCode = ErrorCode.ERRO_INTERNO;

        assertEquals("ERRO_INTERNO", errorCode.getCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, errorCode.getStatus());
    }

    @Test
    @DisplayName("Deve ter todos os códigos de erro definidos")
    void deveTerTodosOsCodigosDeErroDefinidos() {
        // Verifica se todos os códigos de erro essenciais estão presentes
        ErrorCode[] codes = ErrorCode.values();
        assertTrue(codes.length > 0, "Deve haver pelo menos um código de erro definido");

        // Verifica códigos essenciais
        assertNotNull(ErrorCode.VALIDACAO_INVALIDA);
        assertNotNull(ErrorCode.CREDENCIAIS_INVALIDAS);
        assertNotNull(ErrorCode.ACESSO_NEGADO);
        assertNotNull(ErrorCode.RECURSO_NAO_ENCONTRADO);
        assertNotNull(ErrorCode.RECURSO_DUPLICADO);
        assertNotNull(ErrorCode.ERRO_INTERNO);
    }

    @Test
    @DisplayName("Todos os códigos devem ter status HTTP válido")
    void todosOsCodigosDevemTerStatusHttpValido() {
        for (ErrorCode errorCode : ErrorCode.values()) {
            assertNotNull(errorCode.getCode(), "Code não pode ser nulo para: " + errorCode.name());
            assertNotNull(errorCode.getMessage(), "Message não pode ser nula para: " + errorCode.name());
            assertNotNull(errorCode.getStatus(), "Status não pode ser nulo para: " + errorCode.name());
            assertTrue(errorCode.getStatus().is4xxClientError() || errorCode.getStatus().is5xxServerError(),
                    "Status deve ser erro 4xx ou 5xx para: " + errorCode.name());
        }
    }
}