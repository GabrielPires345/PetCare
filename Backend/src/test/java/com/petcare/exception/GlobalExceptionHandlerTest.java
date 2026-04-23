package com.petcare.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        when(request.getRequestURI()).thenReturn("/api/test");
    }

    // ==================== RecursoNaoEncontradoException ====================

    @Test
    @DisplayName("Deve retornar 404 para RecursoNaoEncontradoException")
    void deveRetornar404ParaRecursoNaoEncontrado() {
        RecursoNaoEncontradoException ex = new RecursoNaoEncontradoException("Cliente não encontrado");

        ResponseEntity<ErrorResponse> response = handler.handleRecursoNaoEncontrado(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(ErrorCode.RECURSO_NAO_ENCONTRADO.getCode(), response.getBody().getCode());
        assertEquals("Cliente não encontrado", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    @DisplayName("Deve retornar 404 com nome do recurso e identificador")
    void deveRetornar404ComNomeERecursoIdentificador() {
        RecursoNaoEncontradoException ex = new RecursoNaoEncontradoException("Cliente", "123e4567-e89b-12d3-a456-426614174000");

        ResponseEntity<ErrorResponse> response = handler.handleRecursoNaoEncontrado(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Cliente"));
        assertTrue(response.getBody().getMessage().contains("123e4567-e89b-12d3-a456-426614174000"));
    }

    // ==================== RecursoDuplicadoException ====================

    @Test
    @DisplayName("Deve retornar 409 para RecursoDuplicadoException")
    void deveRetornar409ParaRecursoDuplicado() {
        RecursoDuplicadoException ex = new RecursoDuplicadoException("CPF já cadastrado");

        ResponseEntity<ErrorResponse> response = handler.handleRecursoDuplicado(ex, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(ErrorCode.RECURSO_DUPLICADO.getCode(), response.getBody().getCode());
        assertEquals("CPF já cadastrado", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Deve retornar 409 com detalhes do recurso duplicado")
    void deveRetornar409ComDetalhesDoRecursoDuplicado() {
        RecursoDuplicadoException ex = new RecursoDuplicadoException("Cliente", "CPF", "123.456.789-00");

        ResponseEntity<ErrorResponse> response = handler.handleRecursoDuplicado(ex, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Cliente"));
        assertTrue(response.getBody().getMessage().contains("CPF"));
        assertTrue(response.getBody().getMessage().contains("123.456.789-00"));
    }

    // ==================== NegocioException ====================

    @Test
    @DisplayName("Deve retornar erro de negócio com ErrorCode customizado")
    void deveRetornarErroDeNegocioComErrorCodeCustomizado() {
        NegocioException ex = new NegocioException(ErrorCode.VIOLACAO_INTEGRIDADE, "Não é possível excluir cliente com agendamentos");

        ResponseEntity<ErrorResponse> response = handler.handleNegocioException(ex, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(ErrorCode.VIOLACAO_INTEGRIDADE.getCode(), response.getBody().getCode());
        assertEquals("Não é possível excluir cliente com agendamentos", response.getBody().getMessage());
    }

    // ==================== AutenticacaoException ====================

    @Test
    @DisplayName("Deve retornar 401 para AutenticacaoException")
    void deveRetornar401ParaAutenticacaoException() {
        AutenticacaoException ex = new AutenticacaoException(ErrorCode.CREDENCIAIS_INVALIDAS, "Email ou senha incorretos");

        ResponseEntity<ErrorResponse> response = handler.handleAutenticacaoException(ex, request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(ErrorCode.CREDENCIAIS_INVALIDAS.getCode(), response.getBody().getCode());
    }

    @Test
    @DisplayName("Deve retornar 400 para senhas que não conferem")
    void deveRetornar400ParaSenhasQueNaoConferem() {
        AutenticacaoException ex = new AutenticacaoException(ErrorCode.SENHAS_NAO_CONFEREM, "As senhas não conferem");

        ResponseEntity<ErrorResponse> response = handler.handleAutenticacaoException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(ErrorCode.SENHAS_NAO_CONFEREM.getCode(), response.getBody().getCode());
    }

    // ==================== MethodArgumentNotValidException ====================

    @Test
    @DisplayName("Deve retornar 400 com lista de erros de validação")
    void deveRetornar400ComListaDeErrosDeValidacao() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError1 = new FieldError("object", "email", "Email inválido", false, null, null, "email-invalido");
        FieldError fieldError2 = new FieldError("object", "cpf", "CPF inválido", false, null, null, "123456");

        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<ErrorResponse> response = handler.handleValidacao(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(ErrorCode.VALIDACAO_INVALIDA.getCode(), response.getBody().getCode());
        assertNotNull(response.getBody().getFieldErrors());
        assertEquals(2, response.getBody().getFieldErrors().size());

        List<String> fields = response.getBody().getFieldErrors().stream()
                .map(ErrorResponse.FieldError::getField)
                .toList();
        assertTrue(fields.contains("email"));
        assertTrue(fields.contains("cpf"));
    }

    // ==================== ConstraintViolationException ====================

    @Test
    @DisplayName("Deve retornar 400 para ConstraintViolationException")
    void deveRetornar400ParaConstraintViolationException() {
        Set<ConstraintViolation<?>> violations = new HashSet<>();
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);

        // Mock do PropertyPath
        jakarta.validation.Path path = mock(jakarta.validation.Path.class);
        when(path.toString()).thenReturn("email");

        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("não deve estar em branco");
        when(violation.getInvalidValue()).thenReturn("");
        violations.add(violation);

        ConstraintViolationException ex = new ConstraintViolationException("mensagem", violations);

        ResponseEntity<ErrorResponse> response = handler.handleConstraintViolation(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(ErrorCode.VALIDACAO_INVALIDA.getCode(), response.getBody().getCode());
        assertNotNull(response.getBody().getFieldErrors());
        assertEquals(1, response.getBody().getFieldErrors().size());
    }

    // ==================== HttpMessageNotReadableException ====================

    @Test
    @DisplayName("Deve retornar 400 para JSON malformado")
    void deveRetornar400ParaJSONMalformado() {
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("JSON parse error", null);

        ResponseEntity<ErrorResponse> response = handler.handleHttpMessageNotReadable(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(ErrorCode.REQUISICAO_MALFORMADA.getCode(), response.getBody().getCode());
    }

    // ==================== MissingServletRequestParameterException ====================

    @Test
    @DisplayName("Deve retornar 400 para parâmetro faltando")
    void deveRetornar400ParaParametroFaltando() {
        MissingServletRequestParameterException ex = new MissingServletRequestParameterException("id", "UUID");

        ResponseEntity<ErrorResponse> response = handler.handleMissingParameter(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(ErrorCode.PARAMETRO_FALTANDO.getCode(), response.getBody().getCode());
        assertTrue(response.getBody().getMessage().contains("id"));
    }

    // ==================== MethodArgumentTypeMismatchException ====================

    @Test
    @DisplayName("Deve retornar 400 para tipo de argumento inválido")
    void deveRetornar400ParaTipoDeArgumentoInvalido() {
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getName()).thenReturn("id");
        when(ex.getMessage()).thenReturn("Failed to convert value");

        ResponseEntity<ErrorResponse> response = handler.handleTypeMismatch(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(ErrorCode.VALIDACAO_INVALIDA.getCode(), response.getBody().getCode());
        assertTrue(response.getBody().getMessage().contains("id"));
    }

    // ==================== BadCredentialsException ====================

    @Test
    @DisplayName("Deve retornar 401 para credenciais inválidas")
    void deveRetornar401ParaCredenciaisValidas() {
        BadCredentialsException ex = new BadCredentialsException("Bad credentials");

        ResponseEntity<ErrorResponse> response = handler.handleBadCredentials(ex, request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(ErrorCode.CREDENCIAIS_INVALIDAS.getCode(), response.getBody().getCode());
    }

    // ==================== AuthenticationException ====================

    @Test
    @DisplayName("Deve retornar 401 para erro de autenticação genérico")
    void deveRetornar401ParaErroDeAutenticacaoGenerico() {
        AuthenticationException ex = mock(AuthenticationException.class);
        when(ex.getMessage()).thenReturn("Authentication failed");

        ResponseEntity<ErrorResponse> response = handler.handleAuthenticationException(ex, request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(ErrorCode.CREDENCIAIS_INVALIDAS.getCode(), response.getBody().getCode());
    }

    // ==================== AccessDeniedException ====================

    @Test
    @DisplayName("Deve retornar 403 para acesso negado")
    void deveRetornar403ParaAcessoNegado() {
        AccessDeniedException ex = new AccessDeniedException("Access denied");

        ResponseEntity<ErrorResponse> response = handler.handleAccessDenied(ex, request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(ErrorCode.ACESSO_NEGADO.getCode(), response.getBody().getCode());
    }

    // ==================== DataIntegrityViolationException ====================

    @Test
    @DisplayName("Deve retornar 409 para violação de unique constraint")
    void deveRetornar409ParaViolacaoDeUniqueConstraint() {
        DataIntegrityViolationException ex = mock(DataIntegrityViolationException.class);
        Throwable rootCause = mock(Throwable.class);
        when(rootCause.getMessage()).thenReturn("Duplicate entry 'email@email.com' for key 'uk_email'");
        when(ex.getRootCause()).thenReturn(rootCause);
        when(ex.getMessage()).thenReturn("Data integrity violation");

        ResponseEntity<ErrorResponse> response = handler.handleDataIntegrityViolation(ex, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(ErrorCode.VIOLACAO_INTEGRIDADE.getCode(), response.getBody().getCode());
        assertTrue(response.getBody().getMessage().toLowerCase().contains("único") ||
                  response.getBody().getMessage().contains("existe"));
    }

    @Test
    @DisplayName("Deve retornar 409 para violação de foreign key")
    void deveRetornar409ParaViolacaoDeForeignKey() {
        DataIntegrityViolationException ex = mock(DataIntegrityViolationException.class);
        Throwable rootCause = mock(Throwable.class);
        when(rootCause.getMessage()).thenReturn("FOREIGN KEY constraint fails");
        when(ex.getRootCause()).thenReturn(rootCause);
        when(ex.getMessage()).thenReturn("Data integrity violation");

        ResponseEntity<ErrorResponse> response = handler.handleDataIntegrityViolation(ex, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(ErrorCode.VIOLACAO_INTEGRIDADE.getCode(), response.getBody().getCode());
        assertTrue(response.getBody().getMessage().toLowerCase().contains("referenciado"));
    }

    // ==================== NoHandlerFoundException ====================

    @Test
    @DisplayName("Deve retornar 404 para endpoint não encontrado")
    void deveRetornar404ParaEndpointNaoEncontrado() {
        NoHandlerFoundException ex = mock(NoHandlerFoundException.class);
        when(ex.getRequestURL()).thenReturn("/api/nao-existe");

        ResponseEntity<ErrorResponse> response = handler.handleNoHandlerFound(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(ErrorCode.ENDPOINT_NAO_ENCONTRADO.getCode(), response.getBody().getCode());
    }

    // ==================== HttpRequestMethodNotSupportedException ====================

    @Test
    @DisplayName("Deve retornar 400 para método HTTP não suportado")
    void deveRetornar400ParaMetodoNaoSuportado() {
        HttpRequestMethodNotSupportedException ex = mock(HttpRequestMethodNotSupportedException.class);
        when(ex.getMethod()).thenReturn("DELETE");

        ResponseEntity<ErrorResponse> response = handler.handleMethodNotSupported(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(ErrorCode.VALIDACAO_INVALIDA.getCode(), response.getBody().getCode());
        assertTrue(response.getBody().getMessage().contains("DELETE"));
    }

    // ==================== Exception genérica ====================

    @Test
    @DisplayName("Deve retornar 500 para exceção não tratada")
    void deveRetornar500ParaExcecaoNaoTratada() {
        Exception ex = new RuntimeException("Unexpected error");

        ResponseEntity<ErrorResponse> response = handler.handleGenericException(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(ErrorCode.ERRO_INTERNO.getCode(), response.getBody().getCode());
        assertNotNull(response.getBody().getTimestamp());
    }

    // ==================== Estrutura do ErrorResponse ====================

    @Test
    @DisplayName("ErrorResponse deve conter todos os campos esperados")
    void errorResponseDeveConterTodosOsCamposEsperados() {
        RecursoNaoEncontradoException ex = new RecursoNaoEncontradoException("Recurso não encontrado");

        ResponseEntity<ErrorResponse> response = handler.handleRecursoNaoEncontrado(ex, request);
        ErrorResponse body = response.getBody();

        assertNotNull(body);
        assertNotNull(body.getTimestamp());
        assertEquals(404, body.getStatus());
        assertEquals("Not Found", body.getError());
        assertEquals("RECURSO_NAO_ENCONTRADO", body.getCode());
        assertEquals("Recurso não encontrado", body.getMessage());
        assertEquals("/api/test", body.getPath());
    }
}