package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import jakarta.persistence.*;


import java.time.LocalDateTime;

@Entity
public abstract class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String statement;

    @Enumerated(EnumType.STRING)
    private Type type;

    private int order;

    private LocalDateTime createdAt;

    @ManyToOne
    private Course course;

    public Task() {}

    public Task(String statement, Type type, int order,
                String opentext, Course course) {
        this.statement = statement;
        this.type = type;
        this.order = order;
        this.createdAt = LocalDateTime.now();
        this.course = course;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
