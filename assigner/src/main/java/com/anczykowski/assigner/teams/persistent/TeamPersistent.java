package com.anczykowski.assigner.teams.persistent;

import com.anczykowski.assigner.courses.persistent.CourseEditionPersistent;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_edition_id")
    private CourseEditionPersistent courseEdition;

}
