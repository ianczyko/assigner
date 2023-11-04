package com.anczykowski.assigner.courses.persistent;

import com.anczykowski.assigner.courses.models.CourseEditionGroup;
import com.anczykowski.assigner.courses.repositories.CourseEditionGroupRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class CourseEditionGroupsRepositoryPersistent implements CourseEditionGroupRepository {

    CourseEditionGroupsRepositoryPersistentImpl repositoryImpl;

    ModelMapper modelMapper;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public CourseEditionGroup save(CourseEditionGroup course) {
        var courseEditionPersistent = modelMapper.map(course, CourseEditionGroupPersistent.class);
        var courseEditionPersistentSaved = repositoryImpl.save(courseEditionPersistent);
        return modelMapper.map(courseEditionPersistentSaved, CourseEditionGroup.class);
    }

    @Override
    public List<CourseEditionGroup> getAll(String courseName, String edition) {
        return repositoryImpl.findByCourseEditionCourseNameAndCourseEditionEdition(courseName, edition)
                .stream()
                .map(c -> modelMapper.map(c, CourseEditionGroup.class))
                .toList();
    }

    @Override
    public Optional<CourseEditionGroup> get(
            String courseName,
            String edition,
            String groupName
    ) {
        return repositoryImpl.findByCourseEditionCourseNameAndCourseEditionEditionAndGroupName(
                        courseName,
                        edition,
                        groupName
                )
                .map(ce -> modelMapper.map(ce, CourseEditionGroup.class));
    }

    @Override
    public boolean checkIfUserHasAccessToCourseEditionGroup(
            String courseName,
            String edition,
            String groupName,
            Integer usosId
    ) {
        return repositoryImpl.existsByCourseEditionCourseNameAndCourseEditionEditionAndGroupNameAndUsersUsosId(
                courseName,
                edition,
                groupName,
                usosId
        );
    }
}

@Component
interface CourseEditionGroupsRepositoryPersistentImpl extends JpaRepository<CourseEditionGroupPersistent, Integer> {

    boolean existsByCourseEditionCourseNameAndCourseEditionEditionAndGroupNameAndUsersUsosId(
            String course_name,
            String edition,
            String groupName,
            Integer users_usosId
    );

    List<CourseEditionGroupPersistent> findByCourseEditionCourseNameAndCourseEditionEdition(String courseName, String edition);

    Optional<CourseEditionGroupPersistent> findByCourseEditionCourseNameAndCourseEditionEditionAndGroupName(
            String courseName,
            String edition,
            String groupName
    );
}