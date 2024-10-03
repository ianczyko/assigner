package com.anczykowski.assigner.courses.persistent;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(
        name = "course_edition",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"course_id", "edition"})
        }
)
@Getter
@Setter
@NoArgsConstructor
public class CourseEditionPersistent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    private String edition;

    private Boolean archived;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private CoursePersistent course;

    @OneToMany(mappedBy = "courseEdition")
    @OrderBy("groupName")
    private List<CourseEditionGroupPersistent> courseEditionGroups;

}
