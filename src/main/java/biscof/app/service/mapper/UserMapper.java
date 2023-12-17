package biscof.app.service.mapper;

import biscof.app.dto.UserDto;
import biscof.app.dto.UserResponseDto;
import biscof.app.enums.Role;
import biscof.app.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    @Autowired
    protected PasswordEncoder passwordEncoder;
//
//    @Mapping(target = "rank", expression = "java(calculateRank(link))")
//    public abstract LinkStatsDto toLinkStatsDto(Link link);
//
//    int calculateRank(Link link) {
//        return repository.findRankByRequestCount(link.getRequestCount());
//    }
    public abstract UserDto userToDto(User user);
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
                .build();
    }

}
