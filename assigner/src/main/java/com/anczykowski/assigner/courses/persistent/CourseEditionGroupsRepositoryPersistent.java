package com.anczykowski.assigner.courses.persistent;

import com.anczykowski.assigner.courses.models.CourseEditionGroup;
import com.anczykowski.assigner.courses.models.projections.CourseId;
import com.anczykowski.assigner.courses.repositories.CourseEditionGroupRepository;
import com.anczykowski.assigner.users.models.User;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
                .map(c -> CourseEditionGroup.builder()
                        .id(c.getId())
                        .groupName(c.getGroupName())
                        .build())  // TODO: this method returns omitted field in domain class
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
    public Optional<CourseEditionGroup> getShallow(
            String courseName,
            String edition,
            String groupName
    ) {
        return repositoryImpl.findByCourseEditionCourseNameAndCourseEditionEditionAndGroupName(
                        courseName,
                        edition,
                        groupName
                )
                .map(ce ->
                        CourseEditionGroup.builder()
                                .id(ce.getId())
                                .groupName(ce.getGroupName())
                                .users(ce
                                        .getUsers()
                                        .stream()
                                        .map(u -> User.builder()
                                                .id(u.getId())
                                                .name(u.getName())
                                                .usosId(u.getUsosId())
                                                .secondName(u.getSecondName())
                                                .surname(u.getSurname())
                                                .build())
                                        .collect(Collectors.toSet()))
                                .build());
    }

    @Override
    public Optional<Integer> getId(
            String courseName,
            String edition,
            String groupName
    ) {
        return repositoryImpl.findIdByCourseEditionCourseNameAndCourseEditionEditionAndGroupName(
                        courseName,
                        edition,
                        groupName
                )
                .map(CourseId::getId);
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

    Optional<CourseId> findIdByCourseEditionCourseNameAndCourseEditionEditionAndGroupName(
            String courseName,
            String edition,
            String groupName
    );
}