package com.example.TaskFlow.API.controller;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.example.TaskFlow.API.dto.TaskRequestDto;
import com.example.TaskFlow.API.entity.Project;
import com.example.TaskFlow.API.enums.TaskPriority;
import com.example.TaskFlow.API.enums.TaskStatus;
import com.example.TaskFlow.API.repository.ProjectRepository;
import com.example.TaskFlow.API.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    private Integer savedProjectId;

    @BeforeEach
    void setup() {
        taskRepository.deleteAll();
        projectRepository.deleteAll();

        Project project = new Project();
        project.setName("Integration Test Project");
        Project savedProject = projectRepository.save(project);
        this.savedProjectId = savedProject.getId();
    }

    @Test
    @DisplayName("POST /api/tasks - Should return 201 Created when request is valid")
    @WithMockUser(username = "admin", roles = {"USER"})
    void createTask_ValidPayload_Returns201() throws Exception {
        TaskRequestDto requestDto = new TaskRequestDto();
        requestDto.setTitle("Write Integration Tests");
        requestDto.setDescription("Testing entire flow from Controller to Database");
        requestDto.setStatus(TaskStatus.TODO);
        requestDto.setPriority(TaskPriority.HIGH);
        requestDto.setProjectId(savedProjectId);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Write Integration Tests"))
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.priority").value("HIGH"));
    }

    @Test
    @DisplayName("POST /api/tasks - Should return 400 Bad Request when title is blank")
    @WithMockUser(username = "admin")
    void createTask_InvalidTitle_Returns400() throws Exception {
        TaskRequestDto requestDto = new TaskRequestDto();
        requestDto.setTitle("");
        requestDto.setDescription("Testing entire flow from Controller to Database");
        requestDto.setProjectId(savedProjectId);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/tasks").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/tasks - Should return 200 OK with paginated list")
    @WithMockUser(username = "admin")
    void getTasks_ValidPayload_Returns200() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/tasks").param("page", "0").param("size", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray());
    }
}
