package com.anczykowski.assigner.projects.models;

import com.anczykowski.assigner.courses.models.CourseEditionGroup;
import com.anczykowski.assigner.teams.models.Team;
import com.anczykowski.assigner.users.models.User;
import lombok.*;

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
    private User projectManager;
    private List<ProjectForumComment> comments;
    private List<Team> assignedTeams;

    public Integer getEffectiveLimit() {
        return teamLimit - assignedTeams.stream()
                .filter(Team::getIsAssignmentFinal)
                .mapToInt(i -> 1)
                .sum();
    }

}
