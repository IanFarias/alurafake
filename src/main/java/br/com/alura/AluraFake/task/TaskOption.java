package br.com.alura.AluraFake.task;

import jakarta.persistence.*;

@Entity
public class TaskOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String option;

    @Column
    private boolean isCorrect;

    @ManyToOne
    Task task;

    public TaskOption() {}
}
