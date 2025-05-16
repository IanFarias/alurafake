package br.com.alura.AluraFake.task;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
public class SingleChoiceTask extends Task {

    @OneToMany
    List<TaskOption> options;
}
