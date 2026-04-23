package com.petcare.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Test
    @DisplayName("Deve criar ErrorResponse com método estático of")
    void deveCriarErrorResponseComMetodoEstaticoOf() {
        ErrorResponse response = ErrorResponse.of(ErrorCode.RECURSO_NAO_ENCONTRADO, "/api/clientes/123");

        assertNotNull(response.getTimestamp());
        assertEquals(404, response.getStatus());
        assertEquals("Not Found", response.getError());
        assertEquals("RECURSO_NAO_ENCONTRADO", response.getCode());
        assertEquals("Recurso não encontrado", response.getMessage());
        assertEquals("/api/clientes/123", response.getPath());
        assertNull(response.getFieldErrors());
        assertNull(response.getDetails());
    }

    @Test
    @DisplayName("Deve criar ErrorResponse com mensagem personalizada")
    void deveCriarErrorResponseComMensagemPersonalizada() {
        ErrorResponse response = ErrorResponse.of(
                ErrorCode.RECURSO_DUPLICADO,
                "CPF já cadastrado no sistema",
                "/api/clientes"
        );

        assertEquals(409, response.getStatus());
        assertEquals("RECURSO_DUPLICADO", response.getCode());
        assertEquals("CPF já cadastrado no sistema", response.getMessage());
    }

    @Test
    @DisplayName("Deve criar ErrorResponse com erros de campo")
    void deveCriarErrorResponseComErrosDeCampo() {
        List<ErrorResponse.FieldError> fieldErrors = List.of(
                ErrorResponse.FieldError.builder()
                        .field("email")
                        .message("Email inválido")
                        .rejectedValue("email-invalido")
                        .build(),
                ErrorResponse.FieldError.builder()
                        .field("cpf")
                        .message("CPF inválido")
                        .rejectedValue("123456")
                        .build()
        );

        ErrorResponse response = ErrorResponse.of(
                ErrorCode.VALIDACAO_INVALIDA,
                "/api/clientes",
                fieldErrors
        );

        assertEquals(400, response.getStatus());
        assertNotNull(response.getFieldErrors());
        assertEquals(2, response.getFieldErrors().size());

        ErrorResponse.FieldError emailError = response.getFieldErrors().get(0);
        assertEquals("email", emailError.getField());
        assertEquals("Email inválido", emailError.getMessage());
        assertEquals("email-invalido", emailError.getRejectedValue());
    }

    @Test
    @DisplayName("FieldError deve conter todos os campos")
    void fieldErrorDeveConterTodosOsCampos() {
        ErrorResponse.FieldError fieldError = ErrorResponse.FieldError.builder()
                .field("nome")
                .message("Campo obrigatório")
                .rejectedValue(null)
                .build();

        assertEquals("nome", fieldError.getField());
        assertEquals("Campo obrigatório", fieldError.getMessage());
        assertNull(fieldError.getRejectedValue());
    }

    @Test
    @DisplayName("Deve criar ErrorResponse com builder")
    void deveCriarErrorResponseComBuilder() {
        ErrorResponse response = ErrorResponse.builder()
                .status(400)
                .error("Bad Request")
                .code("CUSTOM_ERROR")
                .message("Mensagem customizada")
                .path("/api/test")
                .build();

        assertEquals(400, response.getStatus());
        assertEquals("Bad Request", response.getError());
        assertEquals("CUSTOM_ERROR", response.getCode());
        assertEquals("Mensagem customizada", response.getMessage());
        assertEquals("/api/test", response.getPath());
    }
}