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
public class CourseEditionGroup {

    Integer id;

    String groupName;

    CourseEdition courseEdition;

    Set<User> users;
}
