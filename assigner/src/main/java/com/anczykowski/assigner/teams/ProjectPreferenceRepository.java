package com.anczykowski.assigner.teams;

import com.anczykowski.assigner.teams.models.ProjectPreference;
import com.anczykowski.assigner.teams.models.ProjectPreferenceId;

public interface ProjectPreferenceRepository {
    ProjectPreference save(ProjectPreference projectPreference);

    ProjectPreference get(ProjectPreferenceId preferenceId);
}
