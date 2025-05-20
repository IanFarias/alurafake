package br.com.alura.AluraFake.task;


import br.com.alura.AluraFake.task.entities.TaskOption;
import br.com.alura.AluraFake.task.validators.OptionLengthValidator;
import br.com.alura.AluraFake.infra.exception.AppException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OptionLengthValidatorTest {

    private final OptionLengthValidator validator = new OptionLengthValidator();

    @Test
    void should_pass_when_all_options_have_valid_length() {
        List<TaskOption> options = List.of(
                new TaskOption("Option valid", true),
                new TaskOption("Another valid option", false)
        );

        assertDoesNotThrow(() -> validator.validate(options, "Some statement"));
    }

    @Test
    void should_throw_when_any_option_is_too_short() {
        List<TaskOption> options = List.of(
                new TaskOption("Ok", true),
                new TaskOption("No", false)
        );

        AppException ex = assertThrows(AppException.class, () ->
                validator.validate(options, "Some statement")
        );
        assertTrue(ex.getMessage().contains("Each option must be between 4 and 80 characters long."));
    }

    @Test
    void should_throw_when_any_option_is_too_long() {
        String longOption = "a".repeat(300);
        List<TaskOption> options = List.of(
                new TaskOption(longOption, true)
        );

        AppException ex = assertThrows(AppException.class, () ->
                validator.validate(options, "Some statement")
        );
        assertTrue(ex.getMessage().contains("Each option must be between 4 and 80 characters long."));
    }
}
