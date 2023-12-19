package biscof.app;

import biscof.app.enums.Priority;
import biscof.app.enums.Role;
import biscof.app.enums.Status;
import biscof.app.model.Comment;
import biscof.app.model.Task;
import biscof.app.model.User;
import biscof.app.repository.CommentRepository;
import biscof.app.repository.TaskRepository;
import biscof.app.repository.UserRepository;
import biscof.app.security.jwt.JwtUtils;
import biscof.app.security.userdetails.UserDetailsServiceImpl;
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
    CommentRepository commentRepository;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserDetailsServiceImpl userDetailsServiceImpl;

    public static final String TEST_USERNAME = "smith@test.com";


    public String provideMockJwt() {
        return jwtUtils.generateToken(userDetailsServiceImpl.loadUserByUsername("smith@test.com"));
    }

    public User provideMockUser() {
        return userRepository.findUserByEmail("smith@test.com").orElseThrow();
    }

    public void initUsers() {
        User use1 = User.builder().firstName("Jane").lastName("Smith").email("smith@test.com")
                .password("$2a$10$8uB.wxOMwjbQWPtZj2vOGODdtPzHWmkcWMh4hUzW7YQSeVNMsvYb.") // 123456789
                .role(Role.USER).tasksAuthored(new ArrayList<>()).tasksToDo(new ArrayList<>()).build();
        userRepository.save(use1);

        User use2 = User.builder().firstName("Jean").lastName("Dupont").email("dupont@test.com")
                .password("$2a$10$2rzM5e1Ndj1BAF8O9aRbLuH9kTI8bTpRB6kdABAov47IeOFHAarpa") // 1qp578g
                .role(Role.USER).tasksAuthored(new ArrayList<>()).tasksToDo(new ArrayList<>()).build();
        userRepository.save(use2);

        User use3 = User.builder().firstName("Ivan").lastName("Petrov").email("petrov@test.com")
                .password("$2a$10$aqLSjg5fvCImTPkDHifBe.Hj1YVbOBlH1Vjtg97Nu9PsA9aR7VrUa") // ry5H85dn
                .role(Role.USER).tasksAuthored(new ArrayList<>()).tasksToDo(new ArrayList<>()).build();
        userRepository.save(use3);
    }

    public void initTasks() {
        User user1 = userRepository.findUserByEmail("smith@test.com").orElseThrow();
        User user2 = userRepository.findUserByEmail("dupont@test.com").orElseThrow();
        User user3 = userRepository.findUserByEmail("petrov@test.com").orElseThrow();

        Task task1 = taskRepository.save(
                Task.builder().title("Fix bugs").description("By Friday").status(Status.NEW)
                        .priority(Priority.MEDIUM).author(user1).executor(user2).build()
        );
        Task task2 = taskRepository.save(
                Task.builder().title("Write tests").description("By tomorrow").status(Status.PENDING)
                        .priority(Priority.HIGH).author(user2).executor(user3).build()
        );
        Task task3 = taskRepository.save(
                Task.builder().title("Go shopping").status(Status.COMPLETED)
                        .priority(Priority.LOW).author(user3).executor(user1).build()
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

    public void initComments() {
        User user1 = userRepository.findUserByEmail("smith@test.com").orElseThrow();
        User user2 = userRepository.findUserByEmail("petrov@test.com").orElseThrow();

        Task task1 = taskRepository.findTaskByTitle("Fix bugs").orElseThrow();
        Task task2 = taskRepository.findTaskByTitle("Write tests").orElseThrow();

        Comment comment1 = Comment.builder().title("Great").content("Thank you!")
                .author(user1).task(task2).build();
        Comment comment2 = Comment.builder().title("Wow").content("That's cool")
                .author(user2).task(task2).build();
        Comment comment3 = Comment.builder().title("Congratulations").content("Great solution")
                .author(user1).task(task2).build();
        Comment comment4 = Comment.builder().title("Ok").content("Done!")
                .author(user2).task(task1).build();

        commentRepository.save(comment1);
        commentRepository.save(comment2);
        commentRepository.save(comment3);
        commentRepository.save(comment4);
    }

    public Long getUserIdByEmail(String userEmail) {
        return userRepository.findUserByEmail(userEmail).orElseThrow().getId();
    }
}
