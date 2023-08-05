package com.anczykowski.assigner.courses.services;

import com.anczykowski.assigner.courses.models.Course;
import com.anczykowski.assigner.courses.repositories.CoursesEditionRepository;
import com.anczykowski.assigner.courses.repositories.CoursesRepository;
import com.anczykowski.assigner.users.UsersService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CoursesService {

    UsersService usersService;

    CoursesRepository coursesRepository;

    CoursesEditionRepository coursesEditionRepository;

    public Course create(String name) {
        var course = Course.builder()
                .name(name)
                .build();
        return coursesRepository.save(course);
    }

    public List<Course> getAll() {
        return coursesRepository.get();
    }
}
