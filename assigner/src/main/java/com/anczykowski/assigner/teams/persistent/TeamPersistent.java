package com.anczykowski.assigner.teams.persistent;

import com.anczykowski.assigner.courses.persistent.CourseEditionGroupPersistent;
import com.anczykowski.assigner.projects.persistent.ProjectPersistent;
import com.anczykowski.assigner.users.persistent.UserPersistent;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "teams")
@Getter
@Setter
@NoArgsConstructor
public class TeamPersistent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    private String name;

    private Integer accessToken;

    private LocalDateTime accessTokenExpirationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_project_id")
    private ProjectPersistent assignedProject;

    private Boolean isAssignmentFinal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_edition_id")
    private CourseEditionGroupPersistent courseEditionGroup;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(
            name = "team_members",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserPersistent> members;

    @OneToMany(mappedBy = "team", cascade = CascadeType.REMOVE)
    private List<ProjectPreferencePersistent> preferences;

}
