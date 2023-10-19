package com.anczykowski.assigner.users.models;

import com.anczykowski.assigner.courses.models.CourseEdition;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "courseEditionsAccess")
public final class User {
    private Integer id;
    private String name;
    private String secondName;
    private String surname;
    private Integer usosId;
    @Builder.Default
    private UserType userType = UserType.STUDENT;
    private Set<CourseEdition> courseEditionsAccess;

    @Override
    public String toString() {
        return "%s %s (%d)".formatted(name, surname, usosId);
    }

    public void addCourseEditionAccess(CourseEdition courseEdition) {
        if (courseEditionsAccess == null) courseEditionsAccess = new HashSet<>();
        courseEditionsAccess.add(courseEdition);
    }
}
