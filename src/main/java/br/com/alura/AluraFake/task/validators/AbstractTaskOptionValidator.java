package br.com.alura.AluraFake.task.validators;

import br.com.alura.AluraFake.task.entities.TaskOption;

import java.util.List;

public abstract class AbstractTaskOptionValidator implements TaskOptionValidator {

    private TaskOptionValidator next;

    public static AbstractTaskOptionValidator link(AbstractTaskOptionValidator first, AbstractTaskOptionValidator... chain) {
        AbstractTaskOptionValidator head = first;
        for (AbstractTaskOptionValidator nextInChain: chain) {
            head.next = nextInChain;
            head = nextInChain;
        }
        return first;
    }

    public abstract void validate(List<TaskOption> taskOptions, String statement);

    protected void checkNext(List<TaskOption> options, String statement) {
        if (next != null) {
            next.validate(options, statement);
        }
    }

}
