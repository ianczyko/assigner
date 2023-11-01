package com.anczykowski.assigner.projects.persistent;

import com.anczykowski.assigner.courses.persistent.CourseEditionGroupPersistent;
import com.anczykowski.assigner.teams.persistent.ProjectPreferencePersistent;
import com.anczykowski.assigner.teams.persistent.TeamPersistent;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
public class ProjectPersistent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    private String name;

    private String description;

    private Integer teamLimit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_edition_id")
    private CourseEditionGroupPersistent courseEditionGroup;

    private String projectManager;

    @OneToMany(mappedBy = "assignedProject")
    private List<TeamPersistent> assignedTeams;

    @OneToMany(mappedBy = "project")
    private List<ProjectPreferencePersistent> preferences;

    @OneToMany(mappedBy = "project")
    private List<ProjectForumCommentPersistent> comments;

}
