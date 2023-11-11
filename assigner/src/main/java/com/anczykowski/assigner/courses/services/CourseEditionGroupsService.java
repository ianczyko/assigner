package com.anczykowski.assigner.courses.services;

import com.anczykowski.assigner.courses.models.CourseEditionGroup;
import com.anczykowski.assigner.courses.repositories.CourseEditionGroupRepository;
import com.anczykowski.assigner.courses.repositories.CourseEditionRepository;
import com.anczykowski.assigner.courses.repositories.CoursesRepository;
import com.anczykowski.assigner.error.NotFoundException;
import com.anczykowski.assigner.users.UsersRepository;
import com.anczykowski.assigner.users.UsersService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class CourseEditionGroupsService {

    UsersService usersService;

    UsersRepository usersRepository;

    CoursesRepository coursesRepository;

    CourseEditionRepository coursesEditionRepository;

    CourseEditionGroupRepository courseEditionGroupRepository;

    public List<CourseEditionGroup> getAll(String courseName, String edition) {
        return courseEditionGroupRepository.getAll(courseName, edition);
    }

    public CourseEditionGroup get(String courseName, String edition, String groupName) {
        return courseEditionGroupRepository.get(courseName, edition, groupName)
                .orElseThrow(() -> new NotFoundException("%s %s course edition not found".formatted(courseName, edition)));
    }

    @Transactional
    public CourseEditionGroup createOrGet(String courseName, String edition, String groupName) {
        var existingCourseEditionGroup = courseEditionGroupRepository.get(courseName, edition, groupName);
        return existingCourseEditionGroup.orElseGet(() -> {
            var courseEdition = coursesEditionRepository.get(courseName, edition)
                    .orElseThrow(() -> new NotFoundException("%s %s course edition not found".formatted(courseName, edition)));
            return courseEditionGroupRepository.save(CourseEditionGroup.builder()
                    .courseEdition(courseEdition)
                    .groupName(groupName)
                    .build());
        });
    }

    @Transactional
    public void reassignUser(Integer usosId, String groupFromName, String groupToName, String courseName, String edition) {
        var user = usersRepository.getByUsosId(usosId)
                .orElseThrow(() -> new NotFoundException("user with usosId %d not found".formatted(usosId)));
        var groupFrom = courseEditionGroupRepository.get(courseName, edition, groupFromName)
                .orElseThrow(() -> new NotFoundException("%s %s %s course edition group not found".formatted(courseName, edition, groupFromName)));
        var groupTo = courseEditionGroupRepository.get(courseName, edition, groupToName)
                .orElseThrow(() -> new NotFoundException("%s %s %s course edition group not found".formatted(courseName, edition, groupFromName)));


        // groupTo.getUsers().add(user);

        groupFrom.getUsers().remove(user);
        user.getCourseEditionGroupsAccess().remove(groupFrom);

        user.getCourseEditionGroupsAccess().add(groupTo);

        courseEditionGroupRepository.save(groupFrom);
        usersRepository.save(user);

        // courseEditionGroupRepository.save(groupTo);
    }
}
