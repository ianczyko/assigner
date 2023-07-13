package com.anczykowski.assigner.courses.dto;

import com.anczykowski.assigner.users.dto.UserDto;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class CourseEditionDto {

    Integer id;

    String edition;

    Set<UserDto> users;

}
