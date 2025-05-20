package br.com.alura.AluraFake.task.validators;

import br.com.alura.AluraFake.task.entities.TaskOption;

import java.util.List;

public interface TaskOptionValidator {
    void validate(List<TaskOption> taskOptions, String statement);
}