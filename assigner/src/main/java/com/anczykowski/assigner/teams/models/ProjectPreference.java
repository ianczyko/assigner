package com.anczykowski.assigner.teams.models;

import com.anczykowski.assigner.projects.models.Project;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public final class ProjectPreference {

    private ProjectPreferenceId id;

    private Project project;

    private Team team;

    private Integer rating;
}
