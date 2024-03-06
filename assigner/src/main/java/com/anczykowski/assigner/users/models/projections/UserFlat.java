package com.anczykowski.assigner.users.models.projections;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public final class UserFlat {
    private Integer id;
    private String name;
    private String secondName;
    private String surname;
}
