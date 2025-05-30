package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.task.dtos.TaskListItemDTO;
import br.com.alura.AluraFake.task.dtos.TaskOptionListItemDTO;
import br.com.alura.AluraFake.task.entities.MultipleChoiceTask;
import br.com.alura.AluraFake.task.entities.SingleChoiceTask;
import br.com.alura.AluraFake.task.entities.Task;
import br.com.alura.AluraFake.task.enums.Type;
import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.user.UserRepository;
import br.com.alura.AluraFake.infra.exception.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository, UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Course createCourse(NewCourseDTO newCourse) {
        //Caso implemente o bonus, pegue o instrutor logado
        Optional<User> possibleAuthor = userRepository
                .findByEmail(newCourse.getEmailInstructor())
                .filter(User::isInstructor);

        if(possibleAuthor.isEmpty()) {
            throw new AppException(HttpStatus.BAD_REQUEST, "User is not a instructor");
        }

        Course course = new Course(newCourse.getTitle(), newCourse.getDescription(), possibleAuthor.get());

        return courseRepository.save(course);
    }

    public List<CourseListItemDTO> listAllCourses() {
        return courseRepository.findAll().stream()
                .map(CourseListItemDTO::new)
                .toList();
    }

    @Transactional
    public Course publishCourse(Long id) {
        Course course = this.findById(id);

        if(course.getStatus() != Status.BUILDING) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Course status invalid.");
        }

        List<Task> tasks = course.getTasks();

        // Verifications
        this.verifyHasAllTypesOfTasks(tasks);
        this.verifyTaskInSequenceOrder(tasks);

        course.setStatus(Status.PUBLISHED);
        course.setPublishedAt(LocalDateTime.now());

        return courseRepository.save(course);
    }

    public CourseDetailedDTO findOne(Long id) {
        Course course = this.findById(id);
        List<Task> tasks = course.getTasks();

        List<TaskListItemDTO> taskListItemDTOS = tasks.stream().map(task -> {
            List<TaskOptionListItemDTO> optionListItemDTOS = null;

            if (task.getType() == Type.SINGLECHOICE) {
                SingleChoiceTask singleChoiceTask = (SingleChoiceTask) task;

                optionListItemDTOS = singleChoiceTask.getOptions()
                        .stream().map(taskOption ->
                                new TaskOptionListItemDTO(taskOption.getOption(), taskOption.isCorrect())).toList();
            }

            if (task.getType() == Type.MULTIPLECHOICE) {
                MultipleChoiceTask multipleChoiceTask = (MultipleChoiceTask) task;

                optionListItemDTOS = multipleChoiceTask.getOptions()
                        .stream().map(taskOption ->
                                new TaskOptionListItemDTO(taskOption.getOption(), taskOption.isCorrect())).toList();
            }

            return new TaskListItemDTO(task.getStatement(), task.getOrder(), optionListItemDTOS);
        }).toList();

        var DTO = new CourseDetailedDTO(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getInstructor().getEmail(),
                course.getStatus(),
                taskListItemDTOS
        );

        return DTO;
    }

    public Course findById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() ->  new AppException(HttpStatus.NOT_FOUND, "Course not found."));

        return course;
    }

    private void verifyTaskInSequenceOrder(List<Task> tasks) {
        List<Integer> orders = tasks.stream()
                .map(Task::getOrder)
                .sorted()
                .toList();

        for(int i = 1; i < orders.size(); i++) {
            if(orders.get(i) != orders.get(i -1) + 1) {
                throw new AppException(HttpStatus.BAD_REQUEST,
                        "The order of activities must be continuous and without skips. (ex: 1, 2, 3...).");
            }
        }
    }

    private void verifyHasAllTypesOfTasks(List<Task>tasks) {
        EnumSet<Type> existingTypes = tasks.stream()
                .map(Task::getType)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(Type.class)));

        EnumSet<Type> requiredTypes = EnumSet.of(Type.OPENTEXT, Type.SINGLECHOICE, Type.MULTIPLECHOICE);

        if (!existingTypes.containsAll(requiredTypes)) {
            throw new AppException(HttpStatus.BAD_REQUEST,
                    "The course must contain at least one activity of each type: OPENTEXT, SINGLECHOICE e MULTIPLECHOICE.");
        }
    }
}
