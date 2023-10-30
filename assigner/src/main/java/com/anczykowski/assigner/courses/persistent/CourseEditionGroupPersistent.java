package com.anczykowski.assigner.courses.persistent;

import com.anczykowski.assigner.projects.persistent.ProjectPersistent;
import com.anczykowski.assigner.teams.persistent.TeamPersistent;
import com.anczykowski.assigner.users.persistent.UserPersistent;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "course_edition_group",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"course_edition_id", "group_name"})
        })
@Getter
@Setter
@NoArgsConstructor
public class CourseEditionGroupPersistent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "group_name")
    private String groupName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_edition_id")
    private CourseEditionPersistent courseEdition;

    @ManyToMany(
            mappedBy = "courseEditionGroupsAccess"
    )
    private Set<UserPersistent> users;


    @OneToMany(mappedBy = "courseEditionGroup")
    private List<TeamPersistent> teams;

    @OneToMany(mappedBy = "courseEditionGroup")
    private List<ProjectPersistent> projects;
}