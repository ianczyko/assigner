package com.anczykowski.assigner.teams.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class TeamDto {

    Integer id;

    @NotBlank
    String name;

}
