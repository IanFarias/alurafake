package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.task.dtos.TaskListItemDTO;

import java.util.List;

public record CourseDetailedDTO(
         Long id,
         String title,
         String description,
         String instructor,
         Status status,
         List<TaskListItemDTO> tasks
) {
}
