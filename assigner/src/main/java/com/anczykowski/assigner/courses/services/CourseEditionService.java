package com.anczykowski.assigner.courses.services;

import com.anczykowski.assigner.courses.models.CourseEdition;
import com.anczykowski.assigner.courses.repositories.CourseEditionRepository;
import com.anczykowski.assigner.courses.repositories.CoursesRepository;
import com.anczykowski.assigner.error.NotFoundException;
import com.anczykowski.assigner.users.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseEditionService {

    final UsersService usersService;

    final CoursesRepository coursesRepository;

    final CourseEditionRepository coursesEditionRepository;

    @Transactional
    public CourseEdition create(
            String courseName,
            String edition
    ) {
        var course = coursesRepository.getByName(courseName)
                .orElseThrow(() -> new NotFoundException("%s course not found".formatted(courseName)));
        var courseEdition = CourseEdition.builder()
                .edition(edition)
                .course(course)
                .build();
        return coursesEditionRepository.save(courseEdition);
    }

    public List<CourseEdition> getAll(String courseName) {
        return coursesEditionRepository.getAll(courseName);
    }

    public CourseEdition get(String courseName, String edition) {
        return coursesEditionRepository.get(courseName, edition)
                .orElseThrow(() -> new NotFoundException("%s %s course edition not found".formatted(courseName, edition)));
    }
}
