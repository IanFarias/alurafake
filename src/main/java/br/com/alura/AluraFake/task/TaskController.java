package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.task.dtos.NewMultipleChoiceTaskDTO;
import br.com.alura.AluraFake.task.dtos.NewOpentextTaskDTO;
import br.com.alura.AluraFake.task.dtos.NewSingleChoiceTaskDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Transactional
    @PostMapping("/task/new/opentext")
    public ResponseEntity newOpenTextExercise(@Valid @RequestBody NewOpentextTaskDTO newOpentextTaskDTO) {
        taskService.newOpentext(newOpentextTaskDTO);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/task/new/singlechoice")
    public ResponseEntity newSingleChoice(@Valid @RequestBody NewSingleChoiceTaskDTO newSinglechoiceTaskDTO) {
        taskService.newSinglechoice(newSinglechoiceTaskDTO);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/task/new/multiplechoice")
    public ResponseEntity newMultipleChoice(@Valid @RequestBody NewMultipleChoiceTaskDTO newMultipleChoiceTaskDTO) {
        taskService.newMultipleChoice(newMultipleChoiceTaskDTO);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}