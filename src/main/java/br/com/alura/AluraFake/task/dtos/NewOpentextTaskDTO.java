package br.com.alura.AluraFake.task.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

public record NewOpentextTaskDTO(
    @NotNull
    Long courseId,
    @NotNull
    @NotBlank
    @Length(min = 4, max = 255)
    String statement,
    @NotNull
    @Positive
    int order
) { }
