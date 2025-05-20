package br.com.alura.AluraFake.task;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.com.alura.AluraFake.course.CourseService;
import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.task.dtos.NewMultipleChoiceTaskDTO;
import br.com.alura.AluraFake.task.dtos.NewOpentextTaskDTO;
import br.com.alura.AluraFake.task.dtos.NewSingleChoiceTaskDTO;
import br.com.alura.AluraFake.task.dtos.NewTaskOptionDTO;
import br.com.alura.AluraFake.task.entities.*;
import br.com.alura.AluraFake.task.enums.Type;
import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.infra.exception.AppException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private CourseService courseService;

    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void newOpentext_shouldCreateTaskSuccessfully() {
        // Arrange
        Long courseId = 1L;
        NewOpentextTaskDTO dto = new NewOpentextTaskDTO(courseId, "Enunciado teste", 1);

        Course course = mock(Course.class);
        when(course.getStatus()).thenReturn(Status.BUILDING);
        when(course.getTasks()).thenReturn(new ArrayList<>());

        when(courseService.findById(courseId)).thenReturn(course);
        when(taskRepository.existsByCourseIdAndOrder(courseId, 0)).thenReturn(false); // Para validar sequência

        // Mock do save retornando o task
        when(taskRepository.save(any(OpenTextTask.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        var task = taskService.newOpentext(dto);

        // Assert
        assertNotNull(task);
        assertEquals(dto.statement(), task.getStatement());
        assertEquals(dto.order(), task.getOrder());
        assertEquals(course, task.getCourse());
        assertEquals("OPENTEXT", task.getType().name());
        verify(taskRepository).save(any(OpenTextTask.class));
    }

    @Test
    void newOpentext_shouldThrowException_whenCourseIsPublished() {
        // Arrange
        Long courseId = 1L;
        NewOpentextTaskDTO dto = new NewOpentextTaskDTO(courseId, "Enunciado teste", 1);

        Course course = mock(Course.class);
        when(course.getStatus()).thenReturn(Status.PUBLISHED);

        when(courseService.findById(courseId)).thenReturn(course);

        // Act & Assert
        AppException exception = assertThrows(AppException.class, () -> taskService.newOpentext(dto));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Course already published.", exception.getMessage());
    }

    @Test
    void newOpentext_shouldThrowException_whenStatementAlreadyExists() {
        // Arrange
        Long courseId = 1L;
        NewOpentextTaskDTO dto = new NewOpentextTaskDTO(courseId, "Enunciado teste", 1);

        Course course = mock(Course.class);
        when(course.getStatus()).thenReturn(Status.BUILDING);

        OpenTextTask existingTask  = new OpenTextTask();
        existingTask.setStatement(dto.statement());
        existingTask.setType(Type.OPENTEXT);
        existingTask.setOrder(dto.order());
        existingTask.setCourse(course);
        existingTask.setCreatedAt(LocalDateTime.now());

        List existingTasks = List.of(existingTask);
        when(course.getTasks()).thenReturn(existingTasks);

        when(courseService.findById(courseId)).thenReturn(course);

        // Act & Assert
        AppException exception = assertThrows(AppException.class, () -> taskService.newOpentext(dto));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("statement already exist for this course.", exception.getMessage());
    }

    @Test
    void newOpentext_shouldThrowException_whenOrderIsInvalid() {
        // Arrange
        Long courseId = 1L;
        NewOpentextTaskDTO dto = new NewOpentextTaskDTO(courseId, "Enunciado teste", 2);

        Course course = mock(Course.class);
        when(course.getStatus()).thenReturn(Status.BUILDING);
        when(course.getTasks()).thenReturn(new ArrayList<>());

        when(courseService.findById(courseId)).thenReturn(course);
        when(taskRepository.existsByCourseIdAndOrder(courseId, 1)).thenReturn(false); // Ordem anterior não existe

        // Act & Assert
        AppException exception = assertThrows(AppException.class, () -> taskService.newOpentext(dto));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Task order invalid.", exception.getMessage());
    }

    @Test
    void newSinglechoice_should_create_task_successfully() {
        // Arrange
        Long courseId = 1L;
        NewSingleChoiceTaskDTO dto = new NewSingleChoiceTaskDTO(
                courseId,
                "Qual a cor do céu?",
                1,
                List.of(
                        new NewTaskOptionDTO("Azul", true),
                        new NewTaskOptionDTO("Verde", false),
                        new NewTaskOptionDTO("Vermelho", false)
                )
        );

        Course course = new Course("Curso Teste", "Descrição", new User("Instrutor", "instrutor@exemplo.com", Role.INSTRUCTOR));
        course.setStatus(Status.BUILDING);

        // Simula curso com tarefas vazias
        course.setTasks(List.of());

        when(courseService.findById(courseId)).thenReturn(course);
        when(taskRepository.existsByCourseIdAndOrder(courseId, dto.order())).thenReturn(false);
        when(taskRepository.findByCourseIdAndOrderGreaterThanEqualOrderByOrderAsc(courseId, dto.order()))
                .thenReturn(List.of());
        when(taskRepository.save(any(SingleChoiceTask.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Task result = taskService.newSinglechoice(dto);

        // Assert
        assertNotNull(result);
        assertEquals(dto.statement(), result.getStatement());
        assertEquals(dto.order(), result.getOrder());
        assertEquals(course, result.getCourse());
        assertEquals(Type.SINGLECHOICE, result.getType());

        // Verifica que salvou no repo
        verify(taskRepository).save(any(SingleChoiceTask.class));
    }

    @Test
    void newSinglechoice_should_throw_when_course_not_building() {
        Long courseId = 1L;
        NewSingleChoiceTaskDTO dto = new NewSingleChoiceTaskDTO(
                courseId,
                "Questão qualquer?",
                1,
                List.of(
                        new NewTaskOptionDTO("Opção 1", true)
                )
        );

        Course course = new Course("Curso", "Desc", new User("Instrutor", "email@x.com", Role.INSTRUCTOR));
        course.setStatus(Status.PUBLISHED);  // status inválido

        when(courseService.findById(courseId)).thenReturn(course);

        AppException ex = assertThrows(AppException.class, () -> taskService.newSinglechoice(dto));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        assertEquals("Course already published.", ex.getMessage());
    }

    @Test
    void newSinglechoice_should_throw_when_order_is_invalid() {
        Long courseId = 1L;
        int invalidOrder = 5;
        NewSingleChoiceTaskDTO dto = new NewSingleChoiceTaskDTO(
                courseId,
                "Questão qualquer?",
                invalidOrder,
                List.of(
                        new NewTaskOptionDTO("Opção 1", true)
                )
        );

        Course course = new Course("Curso", "Desc", new User("Instrutor", "email@x.com", Role.INSTRUCTOR));
        course.setStatus(Status.BUILDING);
        course.setTasks(List.of());

        when(courseService.findById(courseId)).thenReturn(course);
        // Simula que não existe a task anterior (order - 1)
        when(taskRepository.existsByCourseIdAndOrder(courseId, invalidOrder - 1)).thenReturn(false);

        AppException ex = assertThrows(AppException.class, () -> taskService.newSinglechoice(dto));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        assertEquals("Task order invalid.", ex.getMessage());
    }


    @Test
    void newSinglechoice_should_throw_when_statement_already_exists() {
        Long courseId = 1L;
        String duplicateStatement = "Qual a cor do céu?";
        NewSingleChoiceTaskDTO dto = new NewSingleChoiceTaskDTO(
                courseId,
                duplicateStatement,
                1,
                List.of(
                        new NewTaskOptionDTO("Opção 1", true)
                )
        );

        Course course = new Course("Curso", "Desc", new User("Instrutor", "email@x.com", Role.INSTRUCTOR));
        course.setStatus(Status.BUILDING);

        SingleChoiceTask existingTask = new SingleChoiceTask();
        existingTask.setStatement(duplicateStatement);
        existingTask.setType(Type.SINGLECHOICE);
        existingTask.setOrder(1);
        existingTask.setCourse(course);
        existingTask.setCreatedAt(LocalDateTime.now());
        existingTask.setOptions(new ArrayList<TaskOption>());

        course.setTasks(List.of(existingTask));

        when(courseService.findById(courseId)).thenReturn(course);
        when(taskRepository.existsByCourseIdAndOrder(courseId, dto.order())).thenReturn(false);

        AppException ex = assertThrows(AppException.class, () -> taskService.newSinglechoice(dto));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        assertEquals("statement already exist for this course.", ex.getMessage());
    }

    @Test
    void newMultipleChoice_should_create_task_successfully() {
        Long courseId = 1L;
        NewMultipleChoiceTaskDTO dto = new NewMultipleChoiceTaskDTO(
                courseId,
                "Qual cor é o céu?",
                1,
                List.of(
                        new NewTaskOptionDTO("Azul", true),
                        new NewTaskOptionDTO("Verde", true),
                        new NewTaskOptionDTO("Amarelo", false)
                )
        );

        User instructor = new User("Paulo", "paulo@alura.com.br", Role.INSTRUCTOR);
        Course course = new Course("Curso Java", "Descrição", instructor);
        course.setStatus(Status.BUILDING);
        course.setTasks(List.of());  // sem tarefas ainda

        when(courseService.findById(courseId)).thenReturn(course);
        when(taskRepository.existsByCourseIdAndOrder(courseId, dto.order() - 1)).thenReturn(true);
        when(taskRepository.findByCourseIdAndOrderGreaterThanEqualOrderByOrderAsc(courseId, dto.order()))
                .thenReturn(List.of()); // sem tarefas para shift

        // Mock para salvar a task e retornar ela mesma com ID setado (simulação)
        when(taskRepository.save(any(MultipleChoiceTask.class))).thenAnswer(invocation -> {
            MultipleChoiceTask task = invocation.getArgument(0);
            task.setId(123L);
            return task;
        });

        Task createdTask = taskService.newMultipleChoice(dto);

        assertNotNull(createdTask);
        assertEquals(dto.statement(), createdTask.getStatement());
        assertEquals(dto.order(), createdTask.getOrder());
        assertEquals(course, createdTask.getCourse());
        assertEquals(Type.MULTIPLECHOICE, createdTask.getType());

        verify(taskRepository, times(1)).save(any(MultipleChoiceTask.class));
    }

    @Test
    void newMultipleChoice_should_throw_when_course_not_building() {
        Long courseId = 1L;
        NewMultipleChoiceTaskDTO dto = new NewMultipleChoiceTaskDTO(
                courseId,
                "Qual cor é o céu?",
                1,
                List.of(
                        new NewTaskOptionDTO("Azul", true),
                        new NewTaskOptionDTO("Verde", false)
                )
        );

        Course course = new Course("Curso", "Descrição", new User("Instrutor", "email@x.com", Role.INSTRUCTOR));
        course.setStatus(Status.PUBLISHED);  // status inválido

        when(courseService.findById(courseId)).thenReturn(course);

        AppException ex = assertThrows(AppException.class, () -> taskService.newMultipleChoice(dto));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        assertEquals("Course already published.", ex.getMessage());
    }

    @Test
    void newMultipleChoice_should_throw_when_order_is_invalid() {
        Long courseId = 1L;
        int invalidOrder = 5;
        NewMultipleChoiceTaskDTO dto = new NewMultipleChoiceTaskDTO(
                courseId,
                "Questão qualquer?",
                invalidOrder,
                List.of(
                        new NewTaskOptionDTO("Opção 1", true),
                        new NewTaskOptionDTO("Opção 2", false)
                )
        );

        Course course = new Course("Curso", "Descrição", new User("Instrutor", "email@x.com", Role.INSTRUCTOR));
        course.setStatus(Status.BUILDING);
        course.setTasks(List.of());

        when(courseService.findById(courseId)).thenReturn(course);
        when(taskRepository.existsByCourseIdAndOrder(courseId, invalidOrder - 1)).thenReturn(false);

        AppException ex = assertThrows(AppException.class, () -> taskService.newMultipleChoice(dto));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        assertEquals("Task order invalid.", ex.getMessage());
    }

    @Test
    void newMultipleChoice_should_throw_when_statement_already_exists() {
        Long courseId = 1L;
        String duplicateStatement = "Qual a cor do céu?";
        NewMultipleChoiceTaskDTO dto = new NewMultipleChoiceTaskDTO(
                courseId,
                duplicateStatement,
                1,
                List.of(
                        new NewTaskOptionDTO("Azul", true),
                        new NewTaskOptionDTO("Verde", false)
                )
        );

        Course course = new Course("Curso", "Descrição", new User("Instrutor", "email@x.com", Role.INSTRUCTOR));
        course.setStatus(Status.BUILDING);

        MultipleChoiceTask existingTask = new MultipleChoiceTask();
        existingTask.setStatement(duplicateStatement);
        existingTask.setType(Type.MULTIPLECHOICE);
        existingTask.setOrder(1);
        existingTask.setCourse(course);
        existingTask.setCreatedAt(LocalDateTime.now());
        existingTask.setOptions(new ArrayList<TaskOption>());

        course.setTasks(List.of(existingTask));

        when(courseService.findById(courseId)).thenReturn(course);
        when(taskRepository.existsByCourseIdAndOrder(courseId, dto.order())).thenReturn(false);

        AppException ex = assertThrows(AppException.class, () -> taskService.newMultipleChoice(dto));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        assertEquals("statement already exist for this course.", ex.getMessage());
    }
}
