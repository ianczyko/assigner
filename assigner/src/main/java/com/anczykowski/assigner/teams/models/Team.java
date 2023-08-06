package com.anczykowski.assigner.teams.models;

import com.anczykowski.assigner.courses.models.CourseEdition;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public final class Team {
    private Integer id;
    private String name;
    private CourseEdition courseEdition;

}
