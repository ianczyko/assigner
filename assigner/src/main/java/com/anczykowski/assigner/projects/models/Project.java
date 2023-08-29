package com.anczykowski.assigner.projects.models;

import com.anczykowski.assigner.courses.models.CourseEdition;
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
    private CourseEdition courseEdition;
    private List<ProjectForumComment> comments;
}
