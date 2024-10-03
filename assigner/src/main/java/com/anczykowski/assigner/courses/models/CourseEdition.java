package com.anczykowski.assigner.courses.models;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"course", "courseEditionGroups"})
public class CourseEdition {

    Integer id;

    String edition;

    boolean archived = false;

    Course course;

    List<CourseEditionGroup> courseEditionGroups;
}
