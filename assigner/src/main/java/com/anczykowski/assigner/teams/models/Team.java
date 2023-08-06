package com.anczykowski.assigner.teams.models;

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
}
