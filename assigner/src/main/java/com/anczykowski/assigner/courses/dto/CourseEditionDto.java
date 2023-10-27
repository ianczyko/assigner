package com.anczykowski.assigner.courses.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CourseEditionDto {

    Integer id;

    String edition;

    List<CourseEditionGroupShortDto> courseEditionGroups;

}
