package br.com.alura.AluraFake.task.validators;

import br.com.alura.AluraFake.task.entities.TaskOption;
import br.com.alura.AluraFake.util.AppException;
import org.springframework.http.HttpStatus;

import java.util.List;

public class StatementSimilarityValidator extends AbstractTaskOptionValidator{
    @Override
    public void validate(List<TaskOption> taskOptions, String statement) {
        for (var opt : taskOptions) {
            if (opt.getOption().equalsIgnoreCase(statement)) {
                throw new AppException(HttpStatus.BAD_REQUEST, "Options cannot be the same as the statement.");
            }
        }

        this.checkNext(taskOptions, statement);
    }
}
