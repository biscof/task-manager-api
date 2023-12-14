package biscof.app.service.user;

import biscof.app.dto.UserDto;
import biscof.app.dto.UserResponseDto;

import java.util.List;

public interface UserService {

    UserResponseDto getUserById(Long id);

    List<UserResponseDto> getAllUsers();

    UserResponseDto createUser(UserDto userDto);

    UserResponseDto updateUser(Long id, UserDto userDto);

    void deleteUser(Long id);
}
