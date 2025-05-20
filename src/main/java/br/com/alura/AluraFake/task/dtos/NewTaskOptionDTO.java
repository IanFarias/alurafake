package br.com.alura.AluraFake.task.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NewTaskOptionDTO(
        @NotNull
        @NotBlank
        String option,
        @NotNull
        boolean isCorrect
) {
}
