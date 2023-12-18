package biscof.app.service.user;

import biscof.app.dto.user.UserDto;
import biscof.app.dto.user.UserResponseDto;

import java.util.List;

public interface UserService {

    UserResponseDto getUserById(Long id);

    List<UserResponseDto> getAllUsers();

    UserResponseDto createUser(UserDto userDto);

    UserResponseDto updateUser(Long id, UserDto userDto);

    void deleteUser(Long id);
}
