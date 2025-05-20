package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.task.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByCourseIdAndOrderGreaterThanEqualOrderByOrderAsc(Long courseId, int order);
    boolean existsByCourseIdAndOrder(Long courseId, int order);
}
