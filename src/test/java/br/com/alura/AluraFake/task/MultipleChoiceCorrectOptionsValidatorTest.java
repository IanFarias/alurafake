package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.task.entities.TaskOption;
import br.com.alura.AluraFake.task.validators.MultipleChoiceCorrectOptionsValidator;
import br.com.alura.AluraFake.infra.exception.AppException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MultipleChoiceCorrectOptionsValidatorTest {

    private final MultipleChoiceCorrectOptionsValidator validator = new MultipleChoiceCorrectOptionsValidator();

    @Test
    void should_pass_when_at_least_one_option_is_correct() {
        List<TaskOption> options = List.of(
                new TaskOption("Option 1", false),
                new TaskOption("Option 2", true),
                new TaskOption("Option 3", true)
        );

        assertDoesNotThrow(() -> validator.validate(options, "Some statement"));
    }

    @Test
    void should_throw_when_no_option_is_correct() {
        List<TaskOption> options = List.of(
                new TaskOption("Option 1", true),
                new TaskOption("Option 2", true)
        );

        AppException ex = assertThrows(AppException.class, () ->
                validator.validate(options, "Some statement")
        );
        assertTrue(ex.getMessage().contains("Multiple choice requires at least 1 incorrect answer."));
    }
}
