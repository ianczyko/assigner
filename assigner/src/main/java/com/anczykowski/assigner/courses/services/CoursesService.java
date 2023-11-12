package com.anczykowski.assigner.courses.services;

import com.anczykowski.assigner.auth.authutils.AuthUtils;
import com.anczykowski.assigner.courses.models.Course;
import com.anczykowski.assigner.courses.repositories.CourseEditionGroupRepository;
import com.anczykowski.assigner.courses.repositories.CourseEditionRepository;
import com.anczykowski.assigner.courses.repositories.CoursesRepository;
import com.anczykowski.assigner.users.UsersService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class CoursesService {

    UsersService usersService;

    CoursesRepository coursesRepository;

    CourseEditionRepository coursesEditionRepository;

    CourseEditionGroupRepository courseEditionGroupRepository;

    AuthUtils authUtils;

    @Transactional
    public Course create(String name) {
        var course = Course.builder()
                .name(name)
                .build();
        return coursesRepository.save(course);
    }

    public List<Course> getAll() {
        return coursesRepository.get();
    }

    public List<Course> getAllFiltered(Integer usosId) {
        var courses = this.getAll();

        courses.forEach(c -> c.getCourseEditions()
                .forEach(e -> e.setCourseEditionGroups(e.getCourseEditionGroups()
                        .stream().filter(g -> authUtils.hasAccessToCourseEditionGroupNoThrow(c.getName(),
                                e.getEdition(),
                                g.getGroupName(),
                                usosId)
                        ).toList())
                )
        );

        return courses;
    }
}
