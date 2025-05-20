package br.com.alura.AluraFake.task.entities;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.task.enums.Type;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
public class SingleChoiceTask extends Task {

    @OneToMany(mappedBy = "task")
    List<TaskOption> options;

    public SingleChoiceTask() {}

    public SingleChoiceTask(String statement, Type type, int order, Course course, List<TaskOption> options) {
        super(statement, type, order, course);
        this.options = options;
    }
}
