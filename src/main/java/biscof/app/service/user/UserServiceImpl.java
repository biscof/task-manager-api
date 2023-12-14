package biscof.app.service.user;

import biscof.app.dto.UserDto;
import biscof.app.dto.UserResponseDto;
import biscof.app.exception.UserNotFoundException;
import biscof.app.model.User;
import biscof.app.repository.UserRepository;
import biscof.app.exception.DeletionException;
import biscof.app.service.mapper.UserMapper;
//import hexlet.code.model.Role;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    UserMapper userMapper;

    @Override
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException(id)
        );
        return userMapper.userToUserResponseDto(user);
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::userToUserResponseDto)
                .toList();
    }

    @Override
    public UserResponseDto createUser(UserDto userDto) {
        User user = userMapper.userDtoToUser(userDto);
        return userMapper.userToUserResponseDto(userRepository.save(user));
    }

    @Override
    public UserResponseDto updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException(id)
        );
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        return userMapper.userToUserResponseDto(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) {
            throw new UserNotFoundException(id);
        }

        boolean hasNoAssociatedTasks = user.get().getTasksToDo().isEmpty() && user.get().getTasksAuthored().isEmpty();

        if (hasNoAssociatedTasks) {
            userRepository.deleteById(id);
        } else {
            throw new DeletionException(
                    String.format("User with ID %d can't be deleted. There are tasks associated with this user.", id)
            );
        }
    }

}
