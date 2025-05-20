package br.com.alura.AluraFake.task.entities;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.task.enums.Type;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
public class MultipleChoiceTask extends Task{

    @OneToMany(mappedBy = "task")
    List<TaskOption> options;

    public MultipleChoiceTask() {}

    public MultipleChoiceTask(String statement, Type type, int order, Course course, List<TaskOption> options) {
        super(statement, type, order, course);
        this.options = options;
    }

    public List<TaskOption> getOptions() {
        return options;
    }

    public void setOptions(List<TaskOption> options) {
        this.options = options;
    }
}
