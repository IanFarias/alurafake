package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.infra.security.TokenService;
import br.com.alura.AluraFake.task.dtos.*;
import br.com.alura.AluraFake.task.entities.MultipleChoiceTask;
import br.com.alura.AluraFake.task.entities.OpenTextTask;
import br.com.alura.AluraFake.task.entities.SingleChoiceTask;
import br.com.alura.AluraFake.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private TokenService tokenService;

    @Test
    @DisplayName("Deve criar atividade de texto aberto com sucesso")
    void shouldCreateOpenTextTask() throws Exception {
        NewOpentextTaskDTO dto = new NewOpentextTaskDTO(
                1L,
                "Explique o que é polimorfismo",
                1);

        when(taskService.newOpentext(any(NewOpentextTaskDTO.class)))
                .thenReturn(any(OpenTextTask.class));

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Deve criar atividade de alternativa única com sucesso")
    void shouldCreateSingleChoiceTask() throws Exception {
        NewSingleChoiceTaskDTO dto = new NewSingleChoiceTaskDTO(
                1L,
                "Qual é a linguagem do Spring Boot?",
                2,
                new ArrayList<NewTaskOptionDTO>(Arrays.asList(
                        new NewTaskOptionDTO("Java", true),
                        new NewTaskOptionDTO("Python", false),
                        new NewTaskOptionDTO("C#", false))
                )
        );

        when(taskService.newSinglechoice(any(NewSingleChoiceTaskDTO.class)))
                .thenReturn(any(SingleChoiceTask.class));

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Deve criar atividade de múltipla escolha com sucesso")
    void shouldCreateMultipleChoiceTask() throws Exception {
        NewMultipleChoiceTaskDTO dto = new NewMultipleChoiceTaskDTO(
                1L,
                "Qual é a linguagem do Spring Boot?",
                2,
                new ArrayList<NewTaskOptionDTO>(Arrays.asList(
                        new NewTaskOptionDTO("Java", true),
                        new NewTaskOptionDTO("Python", true),
                        new NewTaskOptionDTO("C#", false))
                )
        );

        when(taskService.newMultipleChoice(any(NewMultipleChoiceTaskDTO.class)))
                .thenReturn(any(MultipleChoiceTask.class));

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }
}
