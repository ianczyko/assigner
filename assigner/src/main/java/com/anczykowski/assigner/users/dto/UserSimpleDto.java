package com.anczykowski.assigner.users.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class UserSimpleDto {

    Integer id;

    @NotBlank
    String name;

    String secondName;

    @NotBlank
    String surname;
}
