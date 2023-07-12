package com.anczykowski.assigner.courses.models;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseEdition {

    Integer id;

    String edition;

    Course course;
}
