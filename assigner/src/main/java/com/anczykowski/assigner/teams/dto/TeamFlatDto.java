package com.anczykowski.assigner.teams.dto;

import com.anczykowski.assigner.users.dto.UserSimpleDto;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class TeamFlatDto {

    Integer id;

    @NotBlank
    String name;

    Set<UserSimpleDto> members = new HashSet<>();
}
