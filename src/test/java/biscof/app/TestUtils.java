package biscof.app;

import biscof.app.enums.Priority;
import biscof.app.enums.Role;
import biscof.app.enums.Status;
import biscof.app.model.Task;
import biscof.app.model.User;
import biscof.app.repository.TaskRepository;
import biscof.app.repository.UserRepository;
import biscof.app.security.JwtUtils;
import biscof.app.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TestUtils {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserDetailsServiceImpl userDetailsServiceImpl;

    public static final String TEST_USERNAME = "doe@test.com";


    public String provideMockJwt() {
        return jwtUtils.generateToken(userDetailsServiceImpl.loadUserByUsername("doe@test.com"));
    }

    public User provideMockUser() {
        return userRepository.findUserByEmail("doe@test.com").orElseThrow();
    }

    public void initUsers() {
        User use1 = User.builder().firstName("Jane").lastName("Doe").email("doe@test.com")
                .password("$2a$10$8uB.wxOMwjbQWPtZj2vOGODdtPzHWmkcWMh4hUzW7YQSeVNMsvYb.") // 123456789
                .role(Role.USER).tasksAuthored(new ArrayList<>()).tasksToDo(new ArrayList<>()).build();
        userRepository.save(use1);

        User use2 = User.builder().firstName("Jean").lastName("Dupont").email("dupont@test.com")
                .password("$2a$10$2rzM5e1Ndj1BAF8O9aRbLuH9kTI8bTpRB6kdABAov47IeOFHAarpa") // 1qp578g
                .role(Role.USER).tasksAuthored(new ArrayList<>()).tasksToDo(new ArrayList<>()).build();
        userRepository.save(use2);

        User use3 = User.builder().firstName("Max").lastName("Mustermann").email("mustermann@test.com")
                .password("$2a$10$aqLSjg5fvCImTPkDHifBe.Hj1YVbOBlH1Vjtg97Nu9PsA9aR7VrUa") // ry5H85dn
                .role(Role.USER).tasksAuthored(new ArrayList<>()).tasksToDo(new ArrayList<>()).build();
        userRepository.save(use3);
    }

    public void initTasks() {
        User user1 = userRepository.findUserByEmail("doe@test.com").orElseThrow();
        User user2 = userRepository.findUserByEmail("dupont@test.com").orElseThrow();
        User user3 = userRepository.findUserByEmail("mustermann@test.com").orElseThrow();

        Task task1 = taskRepository.save(
                Task.builder().title("Fix bugs").description("By Friday").status(Status.NEW)
                        .priority(Priority.MEDIUM).author(user1).performer(user2).build()
        );
        Task task2 = taskRepository.save(
                Task.builder().title("Write tests").description("By tomorrow").status(Status.PENDING)
                        .priority(Priority.HIGH).author(user2).performer(user3).build()
        );
        Task task3 = taskRepository.save(
                Task.builder().title("Go shopping").status(Status.COMPLETED)
                        .priority(Priority.LOW).author(user3).performer(user1).build()
        );

        user1.getTasksAuthored().add(task1);
        user2.getTasksAuthored().add(task2);
        user3.getTasksAuthored().add(task3);
        user2.getTasksToDo().add(task1);
        user3.getTasksToDo().addAll(List.of(task2, task3));

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
    }

    public Long getUserIdByEmail(String userEmail) {
        return userRepository.findUserByEmail(userEmail).orElseThrow().getId();
    }
}
