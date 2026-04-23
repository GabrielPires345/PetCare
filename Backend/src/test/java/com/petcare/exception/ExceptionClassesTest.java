package com.petcare.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionClassesTest {

    // ==================== RecursoNaoEncontradoException ====================

    @Test
    @DisplayName("RecursoNaoEncontradoException deve manter mensagem e ErrorCode")
    void recursoNaoEncontradoExceptionDeveManterMensagemEErrorCode() {
        RecursoNaoEncontradoException ex = new RecursoNaoEncontradoException("Cliente não encontrado");

        assertEquals("Cliente não encontrado", ex.getMessage());
        assertEquals(ErrorCode.RECURSO_NAO_ENCONTRADO, ex.getErrorCode());
    }

    @Test
    @DisplayName("RecursoNaoEncontradoException deve aceitar nome do recurso e identificador")
    void recursoNaoEncontradoExceptionDeveAceitarNomeERecursoIdentificador() {
        RecursoNaoEncontradoException ex = new RecursoNaoEncontradoException("Cliente", "abc-123");

        assertTrue(ex.getMessage().contains("Cliente"));
        assertTrue(ex.getMessage().contains("abc-123"));
        assertEquals(ErrorCode.RECURSO_NAO_ENCONTRADO, ex.getErrorCode());
    }

    @Test
    @DisplayName("RecursoNaoEncontradoException deve aceitar ErrorCode customizado")
    void recursoNaoEncontradoExceptionDeveAceitarErrorCodeCustomizado() {
        RecursoNaoEncontradoException ex = new RecursoNaoEncontradoException(
                ErrorCode.ENDPOINT_NAO_ENCONTRADO,
                "Rota não existe"
        );

        assertEquals("Rota não existe", ex.getMessage());
        assertEquals(ErrorCode.ENDPOINT_NAO_ENCONTRADO, ex.getErrorCode());
    }

    // ==================== RecursoDuplicadoException ====================

    @Test
    @DisplayName("RecursoDuplicadoException deve manter mensagem e ErrorCode")
    void recursoDuplicadoExceptionDeveManterMensagemEErrorCode() {
        RecursoDuplicadoException ex = new RecursoDuplicadoException("CPF já cadastrado");

        assertEquals("CPF já cadastrado", ex.getMessage());
        assertEquals(ErrorCode.RECURSO_DUPLICADO, ex.getErrorCode());
    }

    @Test
    @DisplayName("RecursoDuplicadoException deve aceitar dados do recurso")
    void recursoDuplicadoExceptionDeveAceitarDadosDoRecurso() {
        RecursoDuplicadoException ex = new RecursoDuplicadoException("Cliente", "CPF", "123.456.789-00");

        assertTrue(ex.getMessage().contains("Cliente"));
        assertTrue(ex.getMessage().contains("CPF"));
        assertTrue(ex.getMessage().contains("123.456.789-00"));
        assertEquals(ErrorCode.RECURSO_DUPLICADO, ex.getErrorCode());
    }

    @Test
    @DisplayName("RecursoDuplicadoException deve aceitar ErrorCode customizado")
    void recursoDuplicadoExceptionDeveAceitarErrorCodeCustomizado() {
        RecursoDuplicadoException ex = new RecursoDuplicadoException(
                ErrorCode.VIOLACAO_INTEGRIDADE,
                "Violação de integridade"
        );

        assertEquals("Violação de integridade", ex.getMessage());
        assertEquals(ErrorCode.VIOLACAO_INTEGRIDADE, ex.getErrorCode());
    }

    // ==================== NegocioException ====================

    @Test
    @DisplayName("NegocioException deve manter mensagem padrão")
    void negocioExceptionDeveManterMensagemPadrao() {
        NegocioException ex = new NegocioException("Operação não permitida");

        assertEquals("Operação não permitida", ex.getMessage());
        assertEquals(ErrorCode.ERRO_INTERNO, ex.getErrorCode());
    }

    @Test
    @DisplayName("NegocioException deve aceitar ErrorCode customizado")
    void negocioExceptionDeveAceitarErrorCodeCustomizado() {
        NegocioException ex = new NegocioException(
                ErrorCode.VIOLACAO_INTEGRIDADE,
                "Não é possível excluir registro com dependências"
        );

        assertEquals("Não é possível excluir registro com dependências", ex.getMessage());
        assertEquals(ErrorCode.VIOLACAO_INTEGRIDADE, ex.getErrorCode());
    }

    // ==================== AutenticacaoException ====================

    @Test
    @DisplayName("AutenticacaoException deve manter mensagem padrão")
    void autenticacaoExceptionDeveManterMensagemPadrao() {
        AutenticacaoException ex = new AutenticacaoException("Token expirado");

        assertEquals("Token expirado", ex.getMessage());
        assertEquals(ErrorCode.CREDENCIAIS_INVALIDAS, ex.getErrorCode());
    }

    @Test
    @DisplayName("AutenticacaoException deve aceitar ErrorCode customizado")
    void autenticacaoExceptionDeveAceitarErrorCodeCustomizado() {
        AutenticacaoException ex = new AutenticacaoException(
                ErrorCode.TOKEN_INVALIDO,
                "JWT malformado"
        );

        assertEquals("JWT malformado", ex.getMessage());
        assertEquals(ErrorCode.TOKEN_INVALIDO, ex.getErrorCode());
    }

    @Test
    @DisplayName("AutenticacaoException deve aceitar ErrorCode SENHAS_NAO_CONFEREM")
    void autenticacaoExceptionDeveAceitarErrorCodeSenhasNaoConferem() {
        AutenticacaoException ex = new AutenticacaoException(
                ErrorCode.SENHAS_NAO_CONFEREM,
                "As senhas digitadas são diferentes"
        );

        assertEquals("As senhas digitadas são diferentes", ex.getMessage());
        assertEquals(ErrorCode.SENHAS_NAO_CONFEREM, ex.getErrorCode());
    }

    // ==================== Testes de hierarquia ====================

    @Test
    @DisplayName("Todas as exceções devem estender RuntimeException")
    void todasExcecoesDevemEstenderRuntimeException() {
        assertTrue(RuntimeException.class.isAssignableFrom(RecursoNaoEncontradoException.class));
        assertTrue(RuntimeException.class.isAssignableFrom(RecursoDuplicadoException.class));
        assertTrue(RuntimeException.class.isAssignableFrom(NegocioException.class));
        assertTrue(RuntimeException.class.isAssignableFrom(AutenticacaoException.class));
    }
}