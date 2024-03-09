package com.anczykowski.assigner.teams.persistent.projections;

import java.util.Set;

public interface TeamPersistentFlat {
    Integer getId();

    String getName();

    Set<UserProjection> getMembers();

    interface UserProjection {
        Integer getId();

        String getName();

        String getSecondName();

        String getSurname();
    }
}
