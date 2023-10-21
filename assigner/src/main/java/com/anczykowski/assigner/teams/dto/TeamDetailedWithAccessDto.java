package com.anczykowski.assigner.teams.dto;

import com.anczykowski.assigner.projects.dto.ProjectShortDto;
import com.anczykowski.assigner.users.dto.UserSimpleDto;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class TeamDetailedWithAccessDto {

    Integer id;

    @NotBlank
    String name;

    ProjectShortDto assignedProject;

    Set<UserSimpleDto> members;

    Boolean readonly;

}
