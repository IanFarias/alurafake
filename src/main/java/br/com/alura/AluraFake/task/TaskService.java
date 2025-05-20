package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseService;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.task.dtos.NewMultipleChoiceTaskDTO;
import br.com.alura.AluraFake.task.dtos.NewOpentextTaskDTO;
import br.com.alura.AluraFake.task.dtos.NewSingleChoiceTaskDTO;
import br.com.alura.AluraFake.task.entities.*;
import br.com.alura.AluraFake.task.enums.Type;
import br.com.alura.AluraFake.task.validators.*;
import br.com.alura.AluraFake.infra.exception.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final CourseService courseService;

    @Autowired
    public TaskService(TaskRepository taskRepository, CourseService courseService) {
        this.taskRepository = taskRepository;
        this.courseService = courseService;
    }

    @Transactional
    public Task newOpentext(NewOpentextTaskDTO newOpentextTaskDTO) {
        Course course = courseService.findById(newOpentextTaskDTO.courseId());

        if(course.getStatus() != Status.BUILDING) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Course already published.");
        }

        this.validateSequentialOrder(course.getId(), newOpentextTaskDTO.order());

        var statementAlreadyExists = course.getTasks()
                .stream()
                .anyMatch(x -> x.getStatement().equals(newOpentextTaskDTO.statement()));

        if(statementAlreadyExists) {
            throw new AppException(HttpStatus.BAD_REQUEST, "statement already exist for this course.");
        }

        OpenTextTask task = new OpenTextTask (
                newOpentextTaskDTO.statement(),
                Type.OPENTEXT,
                newOpentextTaskDTO.order(),
                course,
                null
        );

        this.evaluateShiftTheTasksOrder(task.getCourse().getId(), task.getOrder());

        return taskRepository.save(task);
    }

    @Transactional
    public Task newSinglechoice(NewSingleChoiceTaskDTO newSinglechoiceTaskDTO) {
        Course course = courseService.findById(newSinglechoiceTaskDTO.courseId());

        if(course.getStatus() != Status.BUILDING) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Course already published.");
        }

        this.validateSequentialOrder(course.getId(), newSinglechoiceTaskDTO.order());

        var statementAlreadyExists = course.getTasks()
                .stream()
                .anyMatch(x -> x.getStatement().equals(newSinglechoiceTaskDTO.statement()));

        if(statementAlreadyExists) {
            throw new AppException(HttpStatus.BAD_REQUEST, "statement already exist for this course.");
        }

        List<TaskOption> options = newSinglechoiceTaskDTO.options()
                .stream()
                .map(op -> new TaskOption(op.option(), op.isCorrect()))
                .toList();

        TaskOptionValidator optionValidators = AbstractTaskOptionValidator.link(
                new SingleChoiceLengthOptionValidator(),
                new SingleChoiceOnlyOneCorrectValidator(),
                new OptionLengthValidator(),
                new UniqueOptionsValidator(),
                new StatementSimilarityValidator()
        );

        optionValidators.validate(options, newSinglechoiceTaskDTO.statement());

        SingleChoiceTask task = new SingleChoiceTask(
                newSinglechoiceTaskDTO.statement(),
                Type.SINGLECHOICE,
                newSinglechoiceTaskDTO.order(),
                course,
                options
        );

        this.evaluateShiftTheTasksOrder(task.getCourse().getId(), task.getOrder());

        return taskRepository.save(task);
    }

    @Transactional
    public Task newMultipleChoice(NewMultipleChoiceTaskDTO newMultipleChoiceTaskDTO) {
        Course course = courseService.findById(newMultipleChoiceTaskDTO.courseId());

        if(course.getStatus() != Status.BUILDING) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Course already published.");
        }

        this.validateSequentialOrder(course.getId(), newMultipleChoiceTaskDTO.order());

        var statementAlreadyExists = course.getTasks()
                .stream()
                .anyMatch(x -> x.getStatement().equals(newMultipleChoiceTaskDTO.statement()));

        if(statementAlreadyExists) {
            throw new AppException(HttpStatus.BAD_REQUEST, "statement already exist for this course.");
        }

        List<TaskOption> options = newMultipleChoiceTaskDTO.options()
                .stream()
                .map(op -> new TaskOption(op.option(), op.isCorrect()))
                .toList();

        TaskOptionValidator optionValidators = AbstractTaskOptionValidator.link(
                new MultipleChoiceLengthOptionValidator(),
                new MultipleChoiceCorrectOptionsValidator(),
                new OptionLengthValidator(),
                new UniqueOptionsValidator(),
                new StatementSimilarityValidator()
        );

        optionValidators.validate(options, newMultipleChoiceTaskDTO.statement());

        MultipleChoiceTask task = new MultipleChoiceTask(
                newMultipleChoiceTaskDTO.statement(),
                Type.MULTIPLECHOICE,
                newMultipleChoiceTaskDTO.order(),
                course,
                options
        );

        this.evaluateShiftTheTasksOrder(task.getCourse().getId(), task.getOrder());

        return taskRepository.save(task);
    }

    @Transactional
    private void evaluateShiftTheTasksOrder(Long courseId, int order) {
        List<Task> tasksToShift = taskRepository.findByCourseIdAndOrderGreaterThanEqualOrderByOrderAsc(
                courseId,
                order
        );

        if(tasksToShift.size() != 0) {
            tasksToShift.forEach(task -> task.setOrder(task.getOrder() + 1));

            taskRepository.saveAll(tasksToShift);
        }
    }

    private void validateSequentialOrder(Long courseId, int order) {
        if(order == 1) return;

        boolean hasNotPreviousTask = !taskRepository.existsByCourseIdAndOrder(courseId, order - 1 );

        if (hasNotPreviousTask) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Task order invalid.");
        }
    }
}
