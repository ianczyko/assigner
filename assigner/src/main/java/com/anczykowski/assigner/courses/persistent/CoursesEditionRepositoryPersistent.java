package com.anczykowski.assigner.courses.persistent;

import com.anczykowski.assigner.courses.models.CourseEdition;
import com.anczykowski.assigner.courses.repositories.CoursesEditionRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@AllArgsConstructor
public class CoursesEditionRepositoryPersistent implements CoursesEditionRepository {

    CoursesEditionRepositoryPersistentImpl repositoryImpl;

    ModelMapper modelMapper;

    @Override
    @Transactional
    public CourseEdition save(CourseEdition course) {
        var courseEditionPersistent = modelMapper.map(course, CourseEditionPersistent.class);
        var courseEditionPersistentSaved = repositoryImpl.save(courseEditionPersistent);
        return modelMapper.map(courseEditionPersistentSaved, CourseEdition.class);
    }

    @Override
    public List<CourseEdition> getAll(String courseName) {
        return repositoryImpl.findByCourseName(courseName)
                .stream()
                .map(c -> modelMapper.map(c, CourseEdition.class))
                .toList();
    }

    @Override
    public CourseEdition get(String courseName, String edition) {
        return modelMapper.map(repositoryImpl.findByCourseNameAndEdition(courseName, edition).get(0), CourseEdition.class);
    }

    @Override
    public boolean checkIfUserHasAccessToCourseEdition(String courseName, String edition, String usosId) {
        return true; // TODO: DB check if user has access to course edition
    }
}

@Component
interface CoursesEditionRepositoryPersistentImpl extends JpaRepository<CourseEditionPersistent, Integer> {
    List<CourseEditionPersistent> findByCourseName(String courseName);

    List<CourseEditionPersistent> findByCourseNameAndEdition(String courseName, String edition);
}