package com.anczykowski.assigner.users.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {

    Integer id;

    @NotBlank
    String name;

    String secondName;

    @NotBlank
    String surname;

    Integer usosId;
}
