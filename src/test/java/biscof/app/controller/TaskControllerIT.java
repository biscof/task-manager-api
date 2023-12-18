package biscof.app.controller;

import biscof.app.TestUtils;
import biscof.app.model.Task;
import biscof.app.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TaskControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    TestUtils testUtils;

    private static final String BASE_TEST_URL = "/api/tasks";
    private static final String TASK_TITLE = "Fix bugs";

    @BeforeEach
    public void setup() {
        testUtils.initUsers();
        testUtils.initTasks();
    }

//    @Test
//    void testGetAllTasks() throws Exception {
//        mockMvc.perform(get(BASE_TEST_URL)
//                        .header("Authorization", "Bearer " + testUtils.provideMockJwt()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$").isArray())
//                .andExpect(jsonPath("$.length()").value(3))
//                .andExpect(jsonPath("$[0].title").value(TASK_TITLE))
//                .andExpect(jsonPath("$[1].status").value("PENDING"));
//    }

//    @Test
//    void testGetTasksByParams() throws Exception {
//        Long taskStatusId = taskStatusRepository.findTaskStatusByName("Cancelled").orElseThrow().getId();
//        Long labelId = labelRepository.findLabelByName("review").orElseThrow().getId();
//
//        MockHttpServletResponse response = mockMvc
//                .perform(get(String.format("%s?taskStatus=%d&labelsId=%d", BASE_TEST_URL, taskStatusId, labelId)))
//                .andReturn()
//                .getResponse();
//
//        List<Task> tasks = MAPPER.readValue(response.getContentAsString(), new TypeReference<>() { });
//
//        assertEquals(200, response.getStatus());
//        assertEquals(1, tasks.size());
//        assertTrue(response.getContentAsString().contains("ivanov@mail.com"));
//        assertTrue(response.getContentAsString().contains(TASK_TITLE));
//        assertFalse(response.getContentAsString().contains("moderate"));
//        assertFalse(response.getContentAsString().contains("Clean up text"));
//    }

    @Test
    void testGetTaskByValidId() throws Exception {
        Task expectedTask = taskRepository.findTaskByTitle(TASK_TITLE).orElseThrow();
        mockMvc.perform(get(BASE_TEST_URL + "/" + expectedTask.getId())
                        .header("Authorization", "Bearer " + testUtils.provideMockJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(TASK_TITLE))
                .andExpect(jsonPath("$.authorName").value("Jane Doe"))
                .andExpect(jsonPath("$.performerName").value("Jean Dupont"))
                .andExpect(jsonPath("$.priority").value("MEDIUM"));
    }

    @Test
    void testGetTaskByInvalidId() throws Exception {
        long invalidId = -1L;
        String errorMessage = "No task found with ID -1.";
        mockMvc.perform(get(BASE_TEST_URL + "/" + invalidId)
                        .header("Authorization", "Bearer " + testUtils.provideMockJwt()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value(errorMessage));
    }

    @Test
    void testCreateTaskValidData() throws Exception {
        String taskDtoJson = """
                {
                    "title": "Fix bug",
                    "description": "By Friday",
                    "status": "NEW",
                    "priority": "LOW"
                }
                """;

        mockMvc.perform(post(BASE_TEST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskDtoJson)
                        .header("Authorization", "Bearer " + testUtils.provideMockJwt()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Fix bug"))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.performerName").isEmpty());

        Optional<Task> testTask = taskRepository.findTaskByTitle("Fix bug");
        assertTrue(testTask.isPresent());
    }

    @Test
    void testCreateTaskInvalidData() throws Exception {
        String taskDtoJson = """
                {
                    "title": "",
                    "priority": "LOW"
                }
                """;
        String errorMessage = "Task title must contain at least one character.";
        mockMvc.perform(post(BASE_TEST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskDtoJson)
                        .header("Authorization", "Bearer " + testUtils.provideMockJwt()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$[0].message").value(errorMessage));
    }

    @Test
    @WithAnonymousUser
    void testCreateTaskUnauthenticated() throws Exception {
        String taskDtoJson = """
                {
                    "title": "",
                    "priority": "LOW"
                }
                """;
        mockMvc.perform(post(BASE_TEST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskDtoJson))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdateTaskByAuthor() throws Exception {
        Task task = taskRepository.findTaskByTitle(TASK_TITLE).orElseThrow();
        String taskDtoJson = """
                {
                    "title": "Write a letter",
                    "status": "IN_PROCESS",
                    "priority": "HIGH"
                }
                """;
        mockMvc.perform(put(BASE_TEST_URL + "/" + task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskDtoJson)
                        .header("Authorization", "Bearer " + testUtils.provideMockJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Write a letter"))
                .andExpect(jsonPath("$.status").value("IN_PROCESS"))
                .andExpect(jsonPath("$.performerName").isEmpty());

        assertNull(task.getDescription());
        assertNull(task.getPerformer());
        assertEquals("Write a letter", task.getTitle());
    }

    @Test
    void testUpdateTaskNotAuthor() throws Exception {
        Task task = taskRepository.findTaskByTitle("Go shopping").orElseThrow();
        String taskDtoJson = """
                {
                    "title": "Write a letter",
                    "status": "IN_PROCESS",
                    "priority": "HIGH"
                }
                """;

        mockMvc.perform(put(BASE_TEST_URL + "/" + task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskDtoJson)
                        .header("Authorization", "Bearer " + testUtils.provideMockJwt()))
                .andExpect(status().isForbidden());

        assertEquals("Go shopping", task.getTitle());
    }

    @Test
    void testDeleteTaskByAuthor() throws Exception {
        Task task = taskRepository.findTaskByTitle(TASK_TITLE).orElseThrow();

        mockMvc.perform(delete(BASE_TEST_URL + "/" + task.getId())
                        .header("Authorization", "Bearer " + testUtils.provideMockJwt()))
                .andExpect(status().isOk());

        assertTrue(taskRepository.findTaskByTitle(TASK_TITLE).isEmpty());
    }

    @Test
    void testDeleteTaskAuthenticatedNotAuthor() throws Exception {
        Task task = taskRepository.findTaskByTitle("Write tests").orElseThrow();

        mockMvc.perform(delete(BASE_TEST_URL + "/" + task.getId())
                        .header("Authorization", "Bearer " + testUtils.provideMockJwt()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testDeleteTaskUnauthenticated() throws Exception {
        Task task = taskRepository.findTaskByTitle(TASK_TITLE).orElseThrow();

        mockMvc.perform(delete(BASE_TEST_URL + "/" + task.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdateTaskStatusAuthor() throws Exception {
        Task task = taskRepository.findTaskByTitle(TASK_TITLE).orElseThrow();
        String statusJson = "{ \"status\": \"IN_PROCESS\" }";

        mockMvc.perform(patch(BASE_TEST_URL + "/" + task.getId() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(statusJson)
                        .header("Authorization", "Bearer " + testUtils.provideMockJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(TASK_TITLE))
                .andExpect(jsonPath("$.status").value("IN_PROCESS"));

        assertEquals("IN_PROCESS", task.getStatus().name());
    }

    @Test
    void testUpdateTaskStatusPerformer() throws Exception {
        Task task = taskRepository.findTaskByTitle("Go shopping").orElseThrow();
        String statusJson = "{ \"status\": \"PENDING\" }";

        mockMvc.perform(patch(BASE_TEST_URL + "/" + task.getId() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(statusJson)
                        .header("Authorization", "Bearer " + testUtils.provideMockJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Go shopping"))
                .andExpect(jsonPath("$.status").value("PENDING"));

        assertEquals("PENDING", task.getStatus().name());
    }

    @Test
    void testUpdateTaskStatusAuthenticatedNotAuthorNotPerformer() throws Exception {
        Task task = taskRepository.findTaskByTitle("Write tests").orElseThrow();
        String statusJson = "{ \"status\": \"IN_PROCESS\" }";

        mockMvc.perform(patch(BASE_TEST_URL + "/" + task.getId() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(statusJson)
                        .header("Authorization", "Bearer " + testUtils.provideMockJwt()))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdateTaskPerformerAuthor() throws Exception {
        Task task = taskRepository.findTaskByTitle(TASK_TITLE).orElseThrow();
        Long newPerformerId = testUtils.getUserIdByEmail("mustermann@test.com");
        String performerJson = String.format("{ \"performerId\": %d }", newPerformerId);

        mockMvc.perform(patch(BASE_TEST_URL + "/" + task.getId() + "/performer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(performerJson)
                        .header("Authorization", "Bearer " + testUtils.provideMockJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(TASK_TITLE))
                .andExpect(jsonPath("$.performerName").value("Max Mustermann"));

        assertEquals(newPerformerId, task.getPerformer().getId());
    }

    @Test
    void testUpdateTaskPerformerAuthenticatedNotAuthor() throws Exception {
        Task task = taskRepository.findTaskByTitle("Write tests").orElseThrow();
        Long newPerformerId = testUtils.getUserIdByEmail("dupont@test.com");
        String performerJson = String.format("{ \"performerId\": %d }", newPerformerId);
        mockMvc.perform(patch(BASE_TEST_URL + "/" + task.getId() + "/performer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(performerJson)
                        .header("Authorization", "Bearer " + testUtils.provideMockJwt()))
                .andExpect(status().isForbidden());
    }

}