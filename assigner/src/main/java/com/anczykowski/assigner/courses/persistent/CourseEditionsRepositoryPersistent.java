package com.anczykowski.assigner.courses.persistent;

import com.anczykowski.assigner.courses.models.CourseEdition;
import com.anczykowski.assigner.courses.repositories.CourseEditionRepository;
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
public class CourseEditionsRepositoryPersistent implements CourseEditionRepository {

    CourseEditionsRepositoryPersistentImpl repositoryImpl;

    ModelMapper modelMapper;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
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
    public Optional<CourseEdition> get(String courseName, String edition) {
        return repositoryImpl.findByCourseNameAndEdition(courseName, edition)
                .map(ce -> modelMapper.map(ce, CourseEdition.class));
    }
}

@Component
interface CourseEditionsRepositoryPersistentImpl extends JpaRepository<CourseEditionPersistent, Integer> {
    List<CourseEditionPersistent> findByCourseName(String courseName);

    Optional<CourseEditionPersistent> findByCourseNameAndEdition(String courseName, String edition);
}