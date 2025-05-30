package br.com.alura.AluraFake.task.entities;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.task.enums.Type;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

@Entity
public class MultipleChoiceTask extends Task{

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    List<TaskOption> options;

    public MultipleChoiceTask() {}

    public MultipleChoiceTask(String statement, int order, Course course) {
        super(statement, Type.MULTIPLECHOICE, order, course);
        this.options = new ArrayList<>();
    }

    public List<TaskOption> getOptions() {
        return options;
    }

    public void setOptions(List<TaskOption> options) {
        this.options = options;
    }
}
