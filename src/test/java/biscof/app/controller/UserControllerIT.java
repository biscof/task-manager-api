package biscof.app.controller;

import biscof.app.TestUtils;
import biscof.app.enums.Priority;
import biscof.app.enums.Status;
import biscof.app.model.Task;
import biscof.app.model.User;
import biscof.app.repository.TaskRepository;
import biscof.app.repository.UserRepository;
import biscof.app.security.JwtUtils;
import biscof.app.security.UserDetailsServiceImpl;
import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.api.DBRider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static biscof.app.TestUtils.TEST_USERNAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DBRider
@DataSet("users.yml")
@DBUnit(schema = "public")
class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    TestUtils testUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    private static final String BASE_TEST_URL = "/api/users";

    @Test
    @WithAnonymousUser
    void testCreateUserValidData() throws Exception {
        final String userDtoJson = """
                    {
                        "firstName": "John",
                        "lastName": "Smith",
                        "email": "smith@test.com",
                        "password": "jUi43#Pn@"
                    }
                """;

        mockMvc.perform(post(BASE_TEST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userDtoJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.email").value("smith@test.com"))
                .andExpect(jsonPath("$.password").doesNotHaveJsonPath())
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    @WithAnonymousUser
    void testCreateUserInvalidData() throws Exception {
        final String userDtoJson = """
                    {
                        "firstName": "",
                        "lastName": "Smith",
                        "email": "@te.c",
                        "password": "jU"
                    }
                """;

        mockMvc.perform(post(BASE_TEST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userDtoJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].code").value(400))
                .andExpect(jsonPath("$[1].status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$[1].message").isNotEmpty())
                .andExpect(jsonPath("$[2].message").isNotEmpty());
    }

    @Test
    @WithMockUser(username = TEST_USERNAME, password = "123456789")
    void testGetUserByValidId() throws Exception {
        mockMvc.perform(get(BASE_TEST_URL + "/" + testUtils.provideMockUser().getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(TEST_USERNAME))
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.password").doesNotHaveJsonPath());
    }

    @Test
    @WithMockUser(username = TEST_USERNAME, password = "123456789")
    void testGetUserByInvalidId() throws Exception {
        final long invalidId = -1L;
        final String userNotFondMsg = "User with ID -1 not found.";

        mockMvc.perform(get(BASE_TEST_URL + "/" + invalidId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value(userNotFondMsg));
    }

    @Test
    @WithAnonymousUser
    void testGetUserUnauthorized() throws Exception {
        mockMvc.perform(get(BASE_TEST_URL + "/" + testUtils.provideMockUser().getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = TEST_USERNAME, password = "123456789")
    void testGetAllUsers() throws Exception {
        mockMvc.perform(get(BASE_TEST_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].firstName").value("Jane"))
                .andExpect(jsonPath("$[1].email").value("dupont@test.com"))
                .andExpect(jsonPath("$[2].lastName").value("Mustermann"));
    }

    @Test
    void testUpdateUser() throws Exception {
        User user = testUtils.provideMockUser();
        final String userDtoJson = """
                    {
                        "firstName": "Olga",
                        "lastName": "Ivanova",
                        "email": "ivanova@mail.com",
                        "password": "or&uuTyN<eC"
                    }
                """;

        mockMvc.perform(put(BASE_TEST_URL + "/" + user.getId())
                        .header("Authorization", "Bearer " + testUtils.provideMockJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userDtoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Olga"))
                .andExpect(jsonPath("$.email").value("ivanova@mail.com"))
                .andExpect(jsonPath("$.password").doesNotHaveJsonPath());

        assertEquals("Olga", user.getFirstName());
        assertEquals("Ivanova", user.getLastName());
        assertNotEquals("oW&uTyN<eC", user.getPassword());
    }

    @Test
    void testDeleteUser() throws Exception {
        mockMvc.perform(delete(BASE_TEST_URL + "/" + testUtils.provideMockUser().getId())
                        .header("Authorization", "Bearer " + testUtils.provideMockJwt()))
                .andExpect(status().isOk());

        assertTrue(userRepository.findUserByEmail(TEST_USERNAME).isEmpty());
    }

    @Test
    void testDeleteUserWithTasks() throws Exception {
        User user = testUtils.provideMockUser();
        Task task = Task.builder()
                .title("Fix bug")
                .description("By Friday")
                .author(user)
                .priority(Priority.MEDIUM)
                .status(Status.NEW)
                .build();
        taskRepository.save(task);
        final String errorMessage = String.format(
                "User with ID %d can't be deleted. There are tasks associated with this user.", user.getId());
        mockMvc.perform(delete(BASE_TEST_URL + "/" + user.getId())
                        .header("Authorization", "Bearer " + testUtils.provideMockJwt()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value("CONFLICT"))
                .andExpect(jsonPath("$.code").value(409))
                .andExpect(jsonPath("$.message").value(errorMessage));
    }
}
