package com.anczykowski.assigner.courses.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CourseDto {

    Integer id;

    String name;

    List<CourseEditionDto> courseEditions;

}
