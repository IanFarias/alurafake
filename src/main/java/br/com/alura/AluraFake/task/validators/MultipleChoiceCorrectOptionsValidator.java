package br.com.alura.AluraFake.task.validators;

import br.com.alura.AluraFake.task.entities.TaskOption;
import br.com.alura.AluraFake.infra.exception.AppException;
import org.springframework.http.HttpStatus;

import java.util.List;

public class MultipleChoiceCorrectOptionsValidator extends AbstractTaskOptionValidator {
    @Override
    public void validate(List<TaskOption> taskOptions, String statement) {

        long correct = taskOptions.stream().filter(TaskOption::isCorrect).count();
        long incorrect = taskOptions.stream().filter(opt -> !opt.isCorrect()).count();

        if (correct < 2) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Multiple choice needs at least 2 correct answers.");
        }
        if (incorrect < 1) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Multiple choice requires at least 1 incorrect answer.");
        }

        this.checkNext(taskOptions, statement);
    }
}
