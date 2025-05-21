package br.com.alura.AluraFake.task.dtos;

import java.util.List;

public record TaskListItemDTO(
        String statement,

        int order,

        List<TaskOptionListItemDTO> options
) { }
