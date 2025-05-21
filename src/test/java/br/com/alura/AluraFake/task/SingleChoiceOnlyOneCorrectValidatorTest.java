package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.task.entities.SingleChoiceTask;
import br.com.alura.AluraFake.task.entities.TaskOption;
import br.com.alura.AluraFake.task.validators.SingleChoiceOnlyOneCorrectValidator;
import br.com.alura.AluraFake.infra.exception.AppException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SingleChoiceOnlyOneCorrectValidatorTest {

    private final SingleChoiceOnlyOneCorrectValidator validator = new SingleChoiceOnlyOneCorrectValidator();

    @Test
    void should_pass_when_exactly_one_option_is_correct() {
        var task = new SingleChoiceTask();

        List<TaskOption> options = List.of(
                new TaskOption("Option 1", false, task),
                new TaskOption("Option 2", true, task),
                new TaskOption("Option 3", false, task)
        );

        assertDoesNotThrow(() -> validator.validate(options, "Some statement"));
    }

    @Test
    void should_throw_when_no_option_is_correct() {
        var task = new SingleChoiceTask();

        List<TaskOption> options = List.of(
                new TaskOption("Option 1", false, task),
                new TaskOption("Option 2", false, task)
        );

        AppException exception = assertThrows(AppException.class, () ->
                validator.validate(options, "Some statement")
        );
        assertTrue(exception.getMessage().contains("There must be exactly one correct alternative."));
    }

    @Test
    void should_throw_when_more_than_one_option_is_correct() {
        var task = new SingleChoiceTask();

        List<TaskOption> options = List.of(
                new TaskOption("Option 1", true, task),
                new TaskOption("Option 2", true, task)
        );

        AppException exception = assertThrows(AppException.class, () ->
                validator.validate(options, "Some statement")
        );
        assertTrue(exception.getMessage().contains("There must be exactly one correct alternative."));
    }
}
