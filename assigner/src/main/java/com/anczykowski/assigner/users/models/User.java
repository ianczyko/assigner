package com.anczykowski.assigner.users.models;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class User {
    private Integer id;
    private String name;
    private String secondName;
    private String surname;
    private Integer usosId;

    @Override
    public String toString() {
        return "%s %s (%d)".formatted(name, surname, usosId);
    }
}
