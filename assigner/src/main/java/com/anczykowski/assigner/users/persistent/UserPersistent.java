package com.anczykowski.assigner.users.persistent;

import com.anczykowski.assigner.courses.persistent.CourseEditionGroupPersistent;
import com.anczykowski.assigner.teams.persistent.TeamPersistent;
import com.anczykowski.assigner.users.models.UserType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class UserPersistent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    private String name;

    @Column(name = "second_name")
    private String secondName;
    private String surname;

    @Column(unique = true)
    private Integer usosId;

    @Enumerated(EnumType.ORDINAL)
    private UserType userType;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(
            name = "course_edition_group_access",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "course_edition_id")
    )
    private Set<CourseEditionGroupPersistent> courseEditionGroupsAccess;

    @ManyToMany(mappedBy = "members")
    private List<TeamPersistent> teamAccesses;

}
