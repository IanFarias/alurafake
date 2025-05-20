package br.com.alura.AluraFake.task.validators;

import br.com.alura.AluraFake.task.entities.TaskOption;
import br.com.alura.AluraFake.infra.exception.AppException;
import org.springframework.http.HttpStatus;

import java.util.List;

public class MultipleChoiceLengthOptionValidator extends AbstractTaskOptionValidator {
    @Override
    public void validate(List<TaskOption> taskOptions, String statement) {
        int min = 3;
        int max = 5;
        int size = taskOptions.size();

        if (size < min || size > max){
            throw new AppException(HttpStatus.BAD_REQUEST,
                    "The activity must have at least 3 and a maximum of 5 options.");
        }

        this.checkNext(taskOptions, statement);
    }
}
