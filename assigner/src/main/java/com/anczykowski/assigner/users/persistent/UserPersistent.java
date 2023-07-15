package com.anczykowski.assigner.users.persistent;

import com.anczykowski.assigner.courses.persistent.CourseEditionPersistent;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private Integer usosId;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(
            name = "course_edition_access",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "course_edition_id")
    )
    private Set<CourseEditionPersistent> courseEditionsAccess;

}