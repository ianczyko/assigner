package com.anczykowski.assigner.teams.persistent;

import com.anczykowski.assigner.projects.persistent.ProjectPersistent;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "project_preferences")
@Getter
@Setter
@NoArgsConstructor
public class ProjectPreferencePersistent {

    @EmbeddedId
    private ProjectPreferencePersistentId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("projectId")
    @JoinColumn(name = "project_id")
    private ProjectPersistent project;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("teamId")
    @JoinColumn(name = "team_id")
    private TeamPersistent team;

    private Integer rating;
}

@Embeddable
@Getter
@Setter
@NoArgsConstructor
class ProjectPreferencePersistentId implements Serializable {
    @Column(name = "project_id")
    private Integer projectId;

    @Column(name = "team_id")
    private Integer teamId;

}
