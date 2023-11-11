package com.anczykowski.assigner.teams.dto;

import com.anczykowski.assigner.projects.dto.ProjectDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public final class ProjectPreferenceDto {

    private ProjectDto project;

    private Integer rating;
}
