package br.com.alura.AluraFake.task;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
public class MultipleChoiceTask extends Task{

    @OneToMany
    List<TaskOption> options;
}
