package com.petcare.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Recurso não encontrado (404)
     */
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleRecursoNaoEncontrado(
            RecursoNaoEncontradoException ex,
            HttpServletRequest request) {
        log.warn("Recurso não encontrado: {}", ex.getMessage());
        return buildResponse(ex.getErrorCode(), ex.getMessage(), request.getRequestURI());
    }

    /**
     * Recurso duplicado (409)
     */
    @ExceptionHandler(RecursoDuplicadoException.class)
    public ResponseEntity<ErrorResponse> handleRecursoDuplicado(
            RecursoDuplicadoException ex,
            HttpServletRequest request) {
        log.warn("Recurso duplicado: {}", ex.getMessage());
        return buildResponse(ex.getErrorCode(), ex.getMessage(), request.getRequestURI());
    }

    /**
     * Exceções de negócio genéricas
     */
    @ExceptionHandler(NegocioException.class)
    public ResponseEntity<ErrorResponse> handleNegocioException(
            NegocioException ex,
            HttpServletRequest request) {
        log.warn("Violação de regra de negócio: {}", ex.getMessage());
        return buildResponse(ex.getErrorCode(), ex.getMessage(), request.getRequestURI());
    }

    /**
     * Exceção de autenticação personalizada (401)
     */
    @ExceptionHandler(AutenticacaoException.class)
    public ResponseEntity<ErrorResponse> handleAutenticacaoException(
            AutenticacaoException ex,
            HttpServletRequest request) {
        log.warn("Erro de autenticação: {}", ex.getMessage());
        return buildResponse(ex.getErrorCode(), ex.getMessage(), request.getRequestURI());
    }

    /**
     * Validação de campos com @Valid (400)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidacao(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        log.warn("Erro de validação: {}", ex.getMessage());

        List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> ErrorResponse.FieldError.builder()
                        .field(error.getField())
                        .message(error.getDefaultMessage())
                        .rejectedValue(error.getRejectedValue())
                        .build())
                .collect(Collectors.toList());

        ErrorResponse response = ErrorResponse.of(
                ErrorCode.VALIDACAO_INVALIDA,
                request.getRequestURI(),
                fieldErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Violação de constraints (validação individual)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request) {
        log.warn("Violação de constraint: {}", ex.getMessage());

        List<ErrorResponse.FieldError> fieldErrors = ex.getConstraintViolations().stream()
                .map(violation -> ErrorResponse.FieldError.builder()
                        .field(extractFieldName(violation))
                        .message(violation.getMessage())
                        .rejectedValue(violation.getInvalidValue())
                        .build())
                .collect(Collectors.toList());

        ErrorResponse response = ErrorResponse.of(
                ErrorCode.VALIDACAO_INVALIDA,
                request.getRequestURI(),
                fieldErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * JSON malformado ou ilegível (400)
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {
        log.warn("JSON malformado: {}", ex.getMessage());
        return buildResponse(ErrorCode.REQUISICAO_MALFORMADA, request.getRequestURI());
    }

    /**
     * Parâmetro obrigatório faltando (400)
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(
            MissingServletRequestParameterException ex,
            HttpServletRequest request) {
        log.warn("Parâmetro faltando: {}", ex.getParameterName());
        String message = String.format("Parâmetro obrigatório '%s' não informado", ex.getParameterName());
        return buildResponse(ErrorCode.PARAMETRO_FALTANDO, message, request.getRequestURI());
    }

    /**
     * Tipo de argumento inválido (ex: UUID inválido) (400)
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {
        log.warn("Tipo de argumento inválido: {}", ex.getMessage());
        String message = String.format("Valor inválido para o parâmetro '%s'", ex.getName());
        return buildResponse(ErrorCode.VALIDACAO_INVALIDA, message, request.getRequestURI());
    }

    /**
     * Credenciais inválidas (401)
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException ex,
            HttpServletRequest request) {
        log.warn("Credenciais inválidas: {}", ex.getMessage());
        return buildResponse(ErrorCode.CREDENCIAIS_INVALIDAS, request.getRequestURI());
    }

    /**
     * Erro de autenticação genérico (401)
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex,
            HttpServletRequest request) {
        log.warn("Erro de autenticação: {}", ex.getMessage());
        return buildResponse(ErrorCode.CREDENCIAIS_INVALIDAS, request.getRequestURI());
    }

    /**
     * Acesso negado (403)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request) {
        log.warn("Acesso negado: {}", ex.getMessage());
        return buildResponse(ErrorCode.ACESSO_NEGADO, request.getRequestURI());
    }

    /**
     * Violação de integridade do banco (FK, unique constraint) (409)
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {
        log.error("Violação de integridade: {}", ex.getMessage());

        String message = extractIntegrityViolationMessage(ex);
        return buildResponse(ErrorCode.VIOLACAO_INTEGRIDADE, message, request.getRequestURI());
    }

    /**
     * Endpoint não encontrado (404)
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFound(
            NoHandlerFoundException ex,
            HttpServletRequest request) {
        log.warn("Endpoint não encontrado: {}", ex.getRequestURL());
        return buildResponse(ErrorCode.ENDPOINT_NAO_ENCONTRADO, request.getRequestURI());
    }

    /**
     * Método HTTP não suportado (405)
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request) {
        log.warn("Método não suportado: {} para {}", ex.getMethod(), request.getRequestURI());
        String message = String.format("Método %s não é suportado para este endpoint", ex.getMethod());
        return buildResponse(ErrorCode.VALIDACAO_INVALIDA, message, request.getRequestURI());
    }

    /**
     * Exceção genérica não tratada (500)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {
        log.error("Erro interno não tratado: ", ex);
        return buildResponse(ErrorCode.ERRO_INTERNO, request.getRequestURI());
    }

    // ================== Métodos auxiliares ==================

    private ResponseEntity<ErrorResponse> buildResponse(ErrorCode errorCode, String path) {
        ErrorResponse response = ErrorResponse.of(errorCode, path);
        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    private ResponseEntity<ErrorResponse> buildResponse(ErrorCode errorCode, String message, String path) {
        ErrorResponse response = ErrorResponse.of(errorCode, message, path);
        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    private String extractFieldName(ConstraintViolation<?> violation) {
        String propertyPath = violation.getPropertyPath().toString();
        return propertyPath.contains(".")
                ? propertyPath.substring(propertyPath.lastIndexOf(".") + 1)
                : propertyPath;
    }

    private String extractIntegrityViolationMessage(DataIntegrityViolationException ex) {
        String rootMessage = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();

        if (rootMessage == null) {
            return "Violação de restrição de integridade do banco de dados";
        }

        // Extrai mensagem mais amigável baseada no tipo de violação
        if (rootMessage.contains("unique") || rootMessage.contains("duplicada") || rootMessage.contains("Duplicate entry")) {
            return "Já existe um registro com este valor único";
        }
        if (rootMessage.contains("foreign key") || rootMessage.contains("FOREIGN KEY")) {
            return "Registro referenciado não encontrado";
        }
        if (rootMessage.contains("not null") || rootMessage.contains("NOT NULL")) {
            return "Campo obrigatório não informado";
        }

        return "Violação de restrição de integridade do banco de dados";
    }
}