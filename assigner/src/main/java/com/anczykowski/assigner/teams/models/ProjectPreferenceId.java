package com.anczykowski.assigner.teams.models;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public final class ProjectPreferenceId {
    private Integer projectId;

    private Integer teamId;
}
