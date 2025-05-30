package br.com.alura.AluraFake.task.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "task_option")
public class TaskOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "`option`")
    private String option;

    @Column
    private boolean isCorrect;

    @ManyToOne
    @JoinColumn(name = "task_id")
    Task task;

    public TaskOption() {}

    public TaskOption(String option, boolean isCorrect, Task task) {
        this.option = option;
        this.isCorrect = isCorrect;
        this.task = task;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
