package br.com.alura.AluraFake.util;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AppException.class)
    public ResponseEntity<AppErrorDTO> handleBusinessException(AppException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(new AppErrorDTO(ex.getStatus(), ex.getMessage()));
    }
}
