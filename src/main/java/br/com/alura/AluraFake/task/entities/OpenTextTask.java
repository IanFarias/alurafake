package br.com.alura.AluraFake.task.entities;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.task.enums.Type;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class OpenTextTask extends Task{

    @Column
    private String opentext;

    public OpenTextTask() {}

    public OpenTextTask(String statement,  int order, Course course, String opentext) {
        super(statement, Type.OPENTEXT, order, course);
        this.opentext = opentext;
    }
}
