package com.anczykowski.assigner.solver.dto;

import com.anczykowski.assigner.teams.dto.TeamDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class AssignOptimizationDto {

    double objective;

    List<TeamDto> teams;

}