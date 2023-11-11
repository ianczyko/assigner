package com.anczykowski.assigner.projects.models;

import com.anczykowski.assigner.courses.models.CourseEditionGroup;
import com.anczykowski.assigner.teams.models.Team;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"courseEditionGroup", "projectManager", "comments", "assignedTeams"})
@ToString(exclude = {"courseEditionGroup", "projectManager", "comments", "assignedTeams"})
public final class Project {
    private Integer id;
    private String name;
    private String description;
    private Integer teamLimit;
    private CourseEditionGroup courseEditionGroup;
    private String projectManager;
    @Builder.Default
    private List<ProjectForumComment> comments = new ArrayList<>();
    @Builder.Default
    private List<Team> assignedTeams = new ArrayList<>();

    public Integer getFinalAssignedTeamsCount() {
        return assignedTeams.stream()
                .filter(Team::getIsAssignmentFinal)
                .mapToInt(i -> 1)
                .sum();
    }

    public Integer getEffectiveLimit() {
        return teamLimit - getFinalAssignedTeamsCount();
    }

}
