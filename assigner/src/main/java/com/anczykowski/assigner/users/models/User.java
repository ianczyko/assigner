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
    private String email;

    @Override
    public String toString() {
        return "%s %s (%s)".formatted(name, surname, email);
    }
}
