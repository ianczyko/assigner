package com.anczykowski.assigner.teams.persistent;

import com.anczykowski.assigner.teams.ProjectPreferenceRepository;
import com.anczykowski.assigner.teams.models.ProjectPreference;
import com.anczykowski.assigner.teams.models.ProjectPreferenceId;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@AllArgsConstructor
public class ProjectPreferenceRepositoryPersistent implements ProjectPreferenceRepository {

    ProjectPreferenceRepositoryPersistentImpl repositoryImpl;

    ModelMapper modelMapper;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public ProjectPreference save(ProjectPreference projectPreference) {
        var projectPreferencePersistent = modelMapper.map(projectPreference, ProjectPreferencePersistent.class);
        var projectPreferencePersistentSaved = repositoryImpl.save(projectPreferencePersistent);
        return modelMapper.map(projectPreferencePersistentSaved, ProjectPreference.class);
    }


    @Override
    public ProjectPreference get(ProjectPreferenceId preferenceId) {
        var preferencePersistentId = modelMapper.map(preferenceId, ProjectPreferencePersistentId.class);
        return modelMapper.map(repositoryImpl.getReferenceById(preferencePersistentId), ProjectPreference.class);
    }
}

@Component
interface ProjectPreferenceRepositoryPersistentImpl extends JpaRepository<ProjectPreferencePersistent, ProjectPreferencePersistentId> {
}