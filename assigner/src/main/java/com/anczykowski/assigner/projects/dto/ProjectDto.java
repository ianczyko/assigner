package com.anczykowski.assigner.projects.dto;

import com.anczykowski.assigner.users.dto.UserSimpleDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class ProjectDto {

    Integer id;

    @NotBlank
    String name;

    @NotNull
    Integer teamLimit;

    UserSimpleDto projectManager;

    String description;

}
