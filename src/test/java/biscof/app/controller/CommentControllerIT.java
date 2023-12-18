package biscof.app.controller;

import biscof.app.TestUtils;
import biscof.app.model.Comment;
import biscof.app.repository.CommentRepository;
import biscof.app.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CommentControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    TestUtils testUtils;

    private static final String BASE_TEST_URL = "/api/comments";

    @BeforeEach
    public void setup() {
        testUtils.initUsers();
        testUtils.initTasks();
        testUtils.initComments();
    }

    @Test
    void testGetAllComments() throws Exception {
        mockMvc.perform(get(BASE_TEST_URL)
                        .header("Authorization", "Bearer " + testUtils.provideMockJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].title").value("Great"))
                .andExpect(jsonPath("$[3].content").value("Done!"));
    }

    @Test
    void getCommentsByAuthorPaginated() throws Exception {
        Long authorId = testUtils.getUserIdByEmail("petrov@test.com");
        String queryString = String.format("?page=0&size=3&authorId=%d", authorId);
        mockMvc.perform(get(BASE_TEST_URL + queryString)
                        .header("Authorization", "Bearer " + testUtils.provideMockJwt()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Wow"))
                .andExpect(jsonPath("$[1].title").value("Ok"))
                .andExpect(jsonPath("$[1].taskTitle").value("Fix bugs"))
                .andExpect(jsonPath("$[0].authorName").value("Ivan Petrov"));
    }

    @Test
    void getAllCommentsByTaskSecondPage() throws Exception {
        Long taskId = taskRepository.findTaskByTitle("Write tests").orElseThrow().getId();
        String queryString = String.format("?page=1&size=2&taskId=%d", taskId);
        mockMvc.perform(get(
                BASE_TEST_URL + queryString)
                        .header("Authorization", "Bearer " + testUtils.provideMockJwt()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Congratulations"))
                .andExpect(jsonPath("$[0].content").value("Great solution"))
                .andExpect(jsonPath("$[0].authorName").value("Jane Smith"))
                .andExpect(jsonPath("$[0].id").isNotEmpty());
    }

    @Test
    void testGetCommentByValidId() throws Exception {
        Comment comment = commentRepository.findCommentByTitle("Great").orElseThrow();
        mockMvc.perform(get(BASE_TEST_URL + "/" + comment.getId())
                        .header("Authorization", "Bearer " + testUtils.provideMockJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Great"))
                .andExpect(jsonPath("$.authorName").value("Jane Smith"))
                .andExpect(jsonPath("$.taskTitle").value("Write tests"));
    }

    @Test
    void testGetCommentByInvalidId() throws Exception {
        long invalidId = -1L;
        String errorMessage = "No comment found with ID -1.";
        mockMvc.perform(get(BASE_TEST_URL + "/" + invalidId)
                        .header("Authorization", "Bearer " + testUtils.provideMockJwt()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value(errorMessage));
    }

    @Test
    void testCreateCommentValidData() throws Exception {
        Long taskId = taskRepository.findTaskByTitle("Go shopping").orElseThrow().getId();
        String commentDtoJson = String.format("""
                {
                    "title": "Hi",
                    "content": "I'll do it",
                    "taskId": %d
                }
                """, taskId);

        mockMvc.perform(post(BASE_TEST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentDtoJson)
                        .header("Authorization", "Bearer " + testUtils.provideMockJwt()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Hi"))
                .andExpect(jsonPath("$.content").value("I'll do it"))
                .andExpect(jsonPath("$.taskTitle").value("Go shopping"))
                .andExpect(jsonPath("$.authorName").value("Jane Smith"));

        assertTrue(commentRepository.findCommentByTitle("Hi").isPresent());
    }

    @Test
    void testCreateTaskInvalidData() throws Exception {
        String taskDtoJson = """
                {
                    "title": "Hi",
                    "content": "Thank you"
                }
                """;
        String errorMessage = "Task ID cannot be empty.";
        mockMvc.perform(post(BASE_TEST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskDtoJson)
                        .header("Authorization", "Bearer " + testUtils.provideMockJwt()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$[0].message").value(errorMessage));
    }

    @Test
    void testUpdateCommentByAuthor() throws Exception {
        Comment comment = commentRepository.findCommentByTitle("Great").orElseThrow();
        String commentDtoJson = String.format("""
                {
                    "title": "Hi",
                    "content": "I'll do it",
                    "taskId": %d
                }
                """, comment.getTask().getId());
        mockMvc.perform(patch(BASE_TEST_URL + "/" + comment.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentDtoJson)
                        .header("Authorization", "Bearer " + testUtils.provideMockJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Hi"))
                .andExpect(jsonPath("$.content").value("I'll do it"))
                .andExpect(jsonPath("$.taskTitle").value("Write tests"));

        assertEquals("I'll do it", comment.getContent());
    }

    @Test
    void testUpdateCommentNotAuthor() throws Exception {
        Comment comment = commentRepository.findCommentByTitle("Wow").orElseThrow();
        String commentDtoJson = String.format("""
                {
                    "title": "Hi",
                    "content": "I'll do it",
                    "taskId": %d
                }
                """, comment.getTask().getId());

        mockMvc.perform(patch(BASE_TEST_URL + "/" + comment.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentDtoJson)
                        .header("Authorization", "Bearer " + testUtils.provideMockJwt()))
                .andExpect(status().isForbidden());

        assertEquals("Wow", comment.getTitle());
    }

    @Test
    void testDeleteCommentByAuthor() throws Exception {
        Comment comment = commentRepository.findCommentByTitle("Great").orElseThrow();

        mockMvc.perform(delete(BASE_TEST_URL + "/" + comment.getId())
                        .header("Authorization", "Bearer " + testUtils.provideMockJwt()))
                .andExpect(status().isOk());

        assertTrue(commentRepository.findCommentByTitle("Great").isEmpty());
    }

    @Test
    void testDeleteTaskAuthenticatedNotAuthor() throws Exception {
        Comment comment = commentRepository.findCommentByTitle("Ok").orElseThrow();

        mockMvc.perform(delete(BASE_TEST_URL + "/" + comment.getId())
                        .header("Authorization", "Bearer " + testUtils.provideMockJwt()))
                .andExpect(status().isForbidden());
    }

}
