package com.anczykowski.assigner.teams.models.projections;

import com.anczykowski.assigner.users.models.projections.UserFlat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
public final class TeamFlat {
    private Integer id;
    private String name;
    @Builder.Default
    private Set<UserFlat> members = new HashSet<>();
}
