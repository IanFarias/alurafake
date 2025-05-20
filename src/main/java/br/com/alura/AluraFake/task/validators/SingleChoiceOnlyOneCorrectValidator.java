package br.com.alura.AluraFake.task.validators;

import br.com.alura.AluraFake.task.entities.TaskOption;
import br.com.alura.AluraFake.util.AppException;
import org.springframework.http.HttpStatus;

import java.util.List;

public class SingleChoiceOnlyOneCorrectValidator extends AbstractTaskOptionValidator{
    @Override
    public void validate(List<TaskOption> taskOptions, String statement) {

        long correct = taskOptions.stream().filter(TaskOption::isCorrect).count();

        if (correct != 1) {
            throw new AppException(HttpStatus.BAD_REQUEST, "There must be exactly one correct alternative.");
        }

        this.checkNext(taskOptions, statement);
    }
}
