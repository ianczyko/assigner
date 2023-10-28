package com.anczykowski.assigner.users.models;

import com.anczykowski.assigner.courses.models.CourseEditionGroup;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "courseEditionGroupsAccess")
public final class User {
    private Integer id;
    private String name;
    private String secondName;
    private String surname;
    private Integer usosId;
    @Builder.Default
    private UserType userType = UserType.STUDENT;
    private Set<CourseEditionGroup> courseEditionGroupsAccess;

    @Override
    public String toString() {
        return "%s %s (%d)".formatted(name, surname, usosId);
    }

    public void addCourseEditionGroupAccess(CourseEditionGroup courseEdition) {
        if (courseEditionGroupsAccess == null) courseEditionGroupsAccess = new HashSet<>();
        courseEditionGroupsAccess.add(courseEdition);
    }
}
