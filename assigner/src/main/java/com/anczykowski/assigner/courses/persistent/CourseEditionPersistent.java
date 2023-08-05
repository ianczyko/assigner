package com.anczykowski.assigner.courses.persistent;

import com.anczykowski.assigner.users.persistent.UserPersistent;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "course_edition")
@Getter
@Setter
@NoArgsConstructor
public class CourseEditionPersistent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    private String edition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private CoursePersistent course;

    @ManyToMany(
            mappedBy = "courseEditionsAccess"
    )
    private Set<UserPersistent> users;
}
