package br.com.alura.AluraFake.task.validators;

import br.com.alura.AluraFake.task.entities.TaskOption;
import br.com.alura.AluraFake.infra.exception.AppException;
import org.springframework.http.HttpStatus;

import java.util.List;

public class OptionLengthValidator extends AbstractTaskOptionValidator {
    @Override
    public void validate(List<TaskOption> taskOptions, String statement) {
        int min = 4;
        int max = 80;

        taskOptions.forEach(opt -> {
            int len = opt.getOption().length();
            if (len < min || len > max) {
                throw new AppException(HttpStatus.BAD_REQUEST, "Each option must be between 4 and 80 characters long.");
            }
        });

        this.checkNext(taskOptions, statement);
    }
}