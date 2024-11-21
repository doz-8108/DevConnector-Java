package org.doz.utils;

import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(BindException.class)
    public ResponseEntity<R> bindExceptionHandler(BindException e) {
        ObjectError error = e.getBindingResult().getAllErrors().get(0);
        log.error("BindException: {}", error.getDefaultMessage());
        return ResponseEntity.badRequest().body(R.builder().message(error.getDefaultMessage()).build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<R> methodArgNotValidExceptionHandler(MethodArgumentNotValidException e) {
        ObjectError error = e.getBindingResult()
                .getAllErrors()
                .get(0);
        log.error("MethodArgumentNotValidException: {}", error.getDefaultMessage());
        return  ResponseEntity.badRequest().body(R.builder().message(error.getDefaultMessage()).build());
    }

    @ExceptionHandler(JwtException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<R> jwtExceptionHandler(Exception exp) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(R.builder().message("Token is invalid, access denied!").build());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<R> generalErrorHandler(Exception exp) {
        log.error("Error occurred: ", exp);
        return ResponseEntity.internalServerError().body(R.builder().message("Oops! Something went wrong! Please contact admin!").build());
    }
}