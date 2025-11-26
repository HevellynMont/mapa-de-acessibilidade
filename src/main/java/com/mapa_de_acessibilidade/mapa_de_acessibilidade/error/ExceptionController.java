package com.mapa_de_acessibilidade.mapa_de_acessibilidade.error;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

record ErrorDTO(Instant timestamp, Integer status, String error, String message) {
}

record ValidationErrorDTO(Instant timestamp, Integer status, String error, List<String> messages) {
}

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorDTO> handleResponseStatus(ResponseStatusException e) {
        ErrorDTO error = new ErrorDTO(Instant.now(), e.getStatusCode().value(), "Erro de Regra de Negócio",
                e.getReason());
        return ResponseEntity.status(e.getStatusCode()).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorDTO> handleValidation(MethodArgumentNotValidException e) {
        List<String> errors = new ArrayList<>();

        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errors.add(fieldError.getDefaultMessage());
        }

        ValidationErrorDTO dto = new ValidationErrorDTO(Instant.now(), 400, "Erro de Validação", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dto);
    }
}