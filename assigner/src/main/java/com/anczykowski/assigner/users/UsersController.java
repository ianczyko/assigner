package com.anczykowski.assigner.users;

import com.anczykowski.assigner.users.dto.UserDto;
import com.anczykowski.assigner.users.models.User;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class UsersController {

    ModelMapper modelMapper;

    UsersService usersService;

    @PostMapping("/users")
    @PreAuthorize("hasAuthority('COORDINATOR')")
    public UserDto newUser(
            @Valid @RequestBody final UserDto userDto
    ) {
        var newUser = modelMapper.map(userDto, User.class);
        var user = usersService.createOrGet(newUser);
        return modelMapper.map(user, UserDto.class);
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('COORDINATOR')")
    public List<UserDto> getUsers() {
        return usersService.getAll()
                .stream()
                .map(c -> modelMapper.map(c, UserDto.class))
                .toList();
    }

    @PutMapping("/users/{usos-id}/role")
    @PreAuthorize("hasAuthority('COORDINATOR')")
    public UserDto changeUserRole(
            @PathVariable("usos-id") Integer usosId,
            @RequestParam("new-role") Integer newRole
    ) {
        var user = usersService.changeRole(usosId, newRole);
        return modelMapper.map(user, UserDto.class);
    }

}
