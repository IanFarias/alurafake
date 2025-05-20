package br.com.alura.AluraFake.task.validators;

import br.com.alura.AluraFake.task.entities.TaskOption;
import br.com.alura.AluraFake.util.AppException;
import org.springframework.http.HttpStatus;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UniqueOptionsValidator extends AbstractTaskOptionValidator {
    @Override
    public void validate(List<TaskOption> taskOptions, String statement) {
        Set<String> seen = new HashSet<>();
        for (var opt : taskOptions) {
            if (!seen.add(opt.getOption())) {
                throw new AppException(HttpStatus.BAD_REQUEST, "The options cannot be equal to each other.");
            }
        }

        this.checkNext(taskOptions, statement);
    }
}
