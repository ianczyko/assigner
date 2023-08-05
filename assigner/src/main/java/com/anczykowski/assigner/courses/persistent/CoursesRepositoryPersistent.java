package com.anczykowski.assigner.courses.persistent;

import com.anczykowski.assigner.courses.repositories.CoursesRepository;
import com.anczykowski.assigner.courses.models.Course;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@AllArgsConstructor
public class CoursesRepositoryPersistent implements CoursesRepository {

    CoursesRepositoryPersistentImpl repositoryImpl;

    ModelMapper modelMapper;

    @Override
    @Transactional
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
    public Course getByName(String courseName) {
        return modelMapper.map(repositoryImpl.findByName(courseName).get(0), Course.class);
    }
}

@Component
interface CoursesRepositoryPersistentImpl extends JpaRepository<CoursePersistent, Integer> {
    List<CoursePersistent> findByName(String name);
}