package br.com.alura.AluraFake.task.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

import java.util.List;

public record NewSingleChoiceTaskDTO(
        @NotNull
        Long courseId,
        @NotNull
        @NotBlank
        @Length(min = 4, max = 255)
        String statement,
        @NotNull
        @Positive
        int order,
        @NotNull
        @Valid
        List<NewTaskOptionDTO> options
) {
}
