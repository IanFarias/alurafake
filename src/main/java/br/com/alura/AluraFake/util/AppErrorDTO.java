package br.com.alura.AluraFake.util;

import org.springframework.http.HttpStatus;

public class AppErrorDTO {
    private int status;
    private String message;

    public AppErrorDTO(HttpStatus status, String message) {
        this.status = status.value();
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status.value();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
