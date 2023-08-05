package com.anczykowski.assigner.courses.models;

import com.anczykowski.assigner.users.models.User;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "users")
public class CourseEdition {

    Integer id;

    String edition;

    Course course;

    Set<User> users;
}
