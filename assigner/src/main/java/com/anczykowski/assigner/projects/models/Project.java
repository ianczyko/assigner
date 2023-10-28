package com.anczykowski.assigner.projects.models;

import com.anczykowski.assigner.courses.models.CourseEditionGroup;
import com.anczykowski.assigner.users.models.User;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public final class Project {
    private Integer id;
    private String name;
    private String description;
    private Integer teamLimit;
    private CourseEditionGroup courseEditionGroup;
    private User projectManager;
    private List<ProjectForumComment> comments;
}
