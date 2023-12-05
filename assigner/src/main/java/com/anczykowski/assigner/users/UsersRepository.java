package com.anczykowski.assigner.users;

import com.anczykowski.assigner.teams.models.Team;
import com.anczykowski.assigner.users.models.User;
import com.anczykowski.assigner.users.persistent.UserPersistent;

import java.util.List;
import java.util.Optional;

public interface UsersRepository {
    User save(User user);

    User get(Integer id);

    Optional<User> getByUsosId(Integer usosId);

    List<Team> getAssignedTeamByUsosId(Integer usosId);

    UserPersistent getUserReferenceById(Integer id);

    List<User> getAll();
}
