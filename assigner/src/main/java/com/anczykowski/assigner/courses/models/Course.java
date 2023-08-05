package com.anczykowski.assigner.courses.models;


import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    Integer id;

    String name;

    List<CourseEdition> courseEditions;
}
