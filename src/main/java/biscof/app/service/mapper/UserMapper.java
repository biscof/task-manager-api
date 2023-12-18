package biscof.app.service.mapper;

import biscof.app.dto.user.UserDto;
import biscof.app.dto.user.UserResponseDto;
import biscof.app.enums.Role;
import biscof.app.model.User;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    @Autowired
    protected PasswordEncoder passwordEncoder;

    public abstract UserResponseDto userToUserResponseDto(User user);

    public User userDtoToUser(UserDto userDto) {
        return User.builder()
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .role(Role.USER)
                .tasksAuthored(new ArrayList<>())
                .tasksToDo(new ArrayList<>())
                .comments(new ArrayList<>())
                .build();
    }

}
