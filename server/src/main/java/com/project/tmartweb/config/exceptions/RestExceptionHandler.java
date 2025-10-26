package com.project.tmartweb.config.exceptions;

import com.project.tmartweb.application.responses.RestData;
import com.project.tmartweb.application.responses.RestResponse;
import jakarta.validation.ConstraintViolationException;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;


@RestControllerAdvice
public class RestExceptionHandler {
    private static final Log LOGGER = LogFactory.getLog(RestExceptionHandler.class);

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<RestData<?>> handleNotFoundException(NotFoundException e) {
        LOGGER.error("NotFoundException: ", e);
        return RestResponse.error(e.getStatus(), e.getUserMessage(), e.getDevMessage());
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<RestData<?>> handleConflictException(ConflictException e) {
        LOGGER.error("ConflictException: " + e);
        return RestResponse.error(e.getStatus(), e.getUserMessage(), e.getDevMessage());
    }

    @ExceptionHandler(InvalidParamException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<RestData<?>> handleInvalidParamException(InvalidParamException e) {
        LOGGER.error("InvalidParamException: ", e);
        return RestResponse.error(e.getStatus(), e.getUserMessage(), e.getDevMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<RestData<?>> handleValidInputException(ConstraintViolationException e) {
        LOGGER.error("ConstraintViolationException: ", e);
        return RestResponse.error(HttpStatus.BAD_REQUEST, e.getMessage(), e.getMessage());
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<RestData<?>> handleBindException(BindException e) {
        List<String> errorMessages = e.getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .toList();
        LOGGER.error("BindException: " + errorMessages);
        return RestResponse.errors(HttpStatus.BAD_REQUEST, errorMessages);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<RestData<?>> handleHttpClientErrorException(HttpClientErrorException e) {
        LOGGER.error("HttpClientErrorException: ", e);
        return RestResponse.error(HttpStatus.FORBIDDEN, e.getMessage(), e.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<RestData<?>> handleUnauthorizedException(UnauthorizedException e) {
        LOGGER.error("UnauthorizedException: ", e);
        return RestResponse.error(e.getStatus(), e.getUserMessage(), e.getDevMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<RestData<?>> handleAccessDeniedSecurityException(AccessDeniedException e) {
        LOGGER.error("AccessDeniedException: ", e);
        return RestResponse.error(HttpStatus.FORBIDDEN,
                "Bạn không có quyền truy cập vào chức năng này",
                e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<RestData<?>> handleRuntimeException(RuntimeException e) {
        LOGGER.error("RuntimeException: ", e);
        return RestResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Đã có lỗi xảy ra!", e.getMessage());
    }
}
