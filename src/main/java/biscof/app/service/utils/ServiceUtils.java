package biscof.app.service.utils;

import biscof.app.exception.exceptions.TaskNotFoundException;
import biscof.app.exception.exceptions.UserNotFoundException;
import biscof.app.model.Task;
import biscof.app.model.User;
import biscof.app.repository.TaskRepository;
import biscof.app.repository.UserRepository;

import biscof.app.security.userdetails.CustomUserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class ServiceUtils {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TaskRepository taskRepository;

    public User getUserFromSecurityContext() {
        return ((CustomUserPrincipal) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal()).getUser();
    }

    public User getPerformer(Long performerId) {
        if (performerId != null) {
            return userRepository
                    .findUserById(performerId)
                    .orElseThrow(() -> new UserNotFoundException(performerId));
        } else {
            return null;
        }
    }

    public String getUsersFullName(User user) {
        if (user == null) {
            return "";
        }
        return String.format("%s %s", user.getFirstName(), user.getLastName());
    }

    public Task getTask(Long id) {
        return taskRepository.findTaskById(id).orElseThrow(
                () -> new TaskNotFoundException(id)
        );
    }

}
