package com.anczykowski.assigner.users;

import com.anczykowski.assigner.users.dto.UserDto;
import com.anczykowski.assigner.users.models.User;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class UsersController {

    ModelMapper modelMapper;

    UsersService usersService;

    @PostMapping("/users")
    public UserDto newUser(
            @Valid @RequestBody final UserDto userDto
    ) {
        var newUser = modelMapper.map(userDto, User.class);
        var user = usersService.createOrGet(newUser);
        return modelMapper.map(user, UserDto.class);
    }
}
