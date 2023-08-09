package com.anczykowski.assigner.courses.persistent;

import com.anczykowski.assigner.courses.models.Course;
import com.anczykowski.assigner.courses.repositories.CoursesRepository;
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
public class CoursesRepositoryPersistent implements CoursesRepository {

    CoursesRepositoryPersistentImpl repositoryImpl;

    ModelMapper modelMapper;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Course save(Course course) {
        var coursePersistent = modelMapper.map(course, CoursePersistent.class);
        var coursePersistentSaved = repositoryImpl.save(coursePersistent);
        return modelMapper.map(coursePersistentSaved, Course.class);
    }

    @Override
    public List<Course> get() {
        return repositoryImpl.findAll()
                .stream()
                .map(c -> modelMapper.map(c, Course.class))
                .toList();
    }

    @Override
    public Optional<Course> getByName(String courseName) {
        return repositoryImpl.findByName(courseName)
                .map(c -> modelMapper.map(c, Course.class));
    }
}

@Component
interface CoursesRepositoryPersistentImpl extends JpaRepository<CoursePersistent, Integer> {
    Optional<CoursePersistent> findByName(String name);
}