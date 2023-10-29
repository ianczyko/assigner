package com.anczykowski.assigner.teams.dto;

import com.anczykowski.assigner.projects.dto.ProjectShortDto;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class TeamDto {

    Integer id;

    @NotBlank
    String name;

    ProjectShortDto assignedProject;

    Boolean isAssignmentFinal = false;

    Integer happiness;

}
