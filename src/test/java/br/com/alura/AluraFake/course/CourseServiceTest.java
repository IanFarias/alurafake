package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.task.entities.Task;
import br.com.alura.AluraFake.task.enums.Type;
import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.user.UserRepository;
import br.com.alura.AluraFake.util.AppException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CourseService courseService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    private List<Task> createTasksWithTypesAndOrders(List<Type> types, List<Integer> orders) {
        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < types.size(); i++) {
            Task task = mock(Task.class);
            when(task.getType()).thenReturn(types.get(i));
            when(task.getOrder()).thenReturn(orders.get(i));
            tasks.add(task);
        }
        return tasks;
    }


    @Test
    void createCourse_shouldThrowException_whenUserIsNotInstructor() {
        // Arrange
        String email = "user@example.com";
        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(new User("Name", email, Role.STUDENT)));

        // Act & Assert
        AppException thrown = assertThrows(AppException.class, () -> {
            courseService.createCourse(new NewCourseDTO("Title", "Desc", email));
        });

        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
        assertEquals("User is not a instructor", thrown.getMessage());
    }

    @Test
    void createCourse_shouldReturnCourse_whenUserIsInstructor() {
        // Arrange
        String email = "instructor@alura.com";
        NewCourseDTO newCourseDTO = new NewCourseDTO();
        newCourseDTO.setTitle("Java");
        newCourseDTO.setDescription("Curso de Java");
        newCourseDTO.setEmailInstructor(email);

        User instructor = new User("Paulo", email, Role.INSTRUCTOR);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(instructor));

        ArgumentCaptor<Course> courseCaptor = ArgumentCaptor.forClass(Course.class);
        when(courseRepository.save(courseCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Course createdCourse = courseService.createCourse(newCourseDTO);

        // Assert
        assertNotNull(createdCourse);
        assertEquals(newCourseDTO.getTitle(), createdCourse.getTitle());
        assertEquals(newCourseDTO.getDescription(), createdCourse.getDescription());
        assertEquals(instructor, createdCourse.getInstructor());
    }

    @Test
    void findById_shouldReturnCourse_whenCourseExists() {
        // Arrange
        Long courseId = 1L;
        User author = new User("Paulo", "paulo@alura.com", Role.INSTRUCTOR);
        Course course = new Course("Java", "Curso de Java", author);
        course.setId(courseId);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        // Act
        Course foundCourse = courseService.findById(courseId);

        // Assert
        assertNotNull(foundCourse);
        assertEquals(courseId, foundCourse.getId());
        assertEquals("Java", foundCourse.getTitle());
        assertEquals("Curso de Java", foundCourse.getDescription());
        assertEquals(author, foundCourse.getInstructor());
    }

    @Test
    void findById_shouldThrowException_whenCourseDoesNotExist() {
        // Arrange
        Long courseId = 999L;
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        // Act & Assert
        AppException exception = assertThrows(AppException.class, () -> {
            courseService.findById(courseId);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Course not found.", exception.getMessage());
    }

    @Test
    void publishCourse_shouldPublishSuccessfully_whenAllValidationsPass() {
        // Arrange
        User author = new User("Paulo", "paulo@alura.com", Role.INSTRUCTOR);

        Course course = spy(new Course("Java", "Desc", author));
        course.setId(1L);
        course.setStatus(Status.BUILDING);

        // Tasks com todos os tipos e ordem sequencial
        List<Task> tasks = createTasksWithTypesAndOrders(
                Arrays.asList(Type.OPENTEXT, Type.SINGLECHOICE, Type.MULTIPLECHOICE),
                Arrays.asList(1, 2, 3)
        );

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(course.getTasks()).thenReturn(tasks);

        when(courseRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // Act
        Course publishedCourse = courseService.publishCourse(1L);

        // Assert
        assertEquals(Status.PUBLISHED, publishedCourse.getStatus());
        assertNotNull(publishedCourse.getPublishedAt());
        verify(courseRepository).save(course);
    }
    @Test
    void publishCourse_shouldThrowException_whenCourseStatusIsInvalid() {
        // Arrange
        User author = new User("Paulo", "paulo@alura.com", Role.INSTRUCTOR);
        Course course = spy(new Course("Java", "Desc", author));
        course.setId(1L);
        course.setStatus(Status.PUBLISHED); // status inválido para publicar

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        // Act & Assert
        AppException exception = assertThrows(AppException.class, () -> {
            courseService.publishCourse(1L);
        });
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Course status invalid.", exception.getMessage());
    }

    @Test
    void publishCourse_shouldThrowException_whenCourseDoesNotHaveAllTypesOfTasks() {
        // Arrange
        User author = new User("Paulo", "paulo@alura.com", Role.INSTRUCTOR);
        Course course = spy(new Course("Java", "Desc", author));
        course.setId(1L);
        course.setStatus(Status.BUILDING);

        List<Task> tasks = createTasksWithTypesAndOrders(
                Arrays.asList(Type.OPENTEXT, Type.SINGLECHOICE),
                Arrays.asList(1, 2)
        );

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(course.getTasks()).thenReturn(tasks);

        // Act & Assert
        AppException exception = assertThrows(AppException.class, () -> {
            courseService.publishCourse(1L);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertTrue(exception.getMessage().contains("must contain at least one activity"));
    }

    @Test
    void publishCourse_shouldThrowException_whenTaskOrdersAreNotSequential() {
        // Arrange
        User author = new User("Paulo", "paulo@alura.com", Role.INSTRUCTOR);
        Course course = spy(new Course("Java", "Desc", author));
        course.setId(1L);
        course.setStatus(Status.BUILDING);

        // Tasks com todos os tipos mas ordem com salto (ex: 1,3,4)
        List<Task> tasks = createTasksWithTypesAndOrders(
                Arrays.asList(Type.OPENTEXT, Type.SINGLECHOICE, Type.MULTIPLECHOICE),
                Arrays.asList(1, 3, 4)
        );

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(course.getTasks()).thenReturn(tasks);

        // Act & Assert
        AppException exception = assertThrows(AppException.class, () -> {
            courseService.publishCourse(1L);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertTrue(exception.getMessage().contains("order of activities must be continuous"));
    }
}
