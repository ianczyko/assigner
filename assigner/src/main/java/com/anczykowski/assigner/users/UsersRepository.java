package com.anczykowski.assigner.users;

import com.anczykowski.assigner.users.models.User;

import java.util.Optional;

public interface UsersRepository {
    User save(User user);

    User get(Integer id);

    Optional<User> getByUsosId(Integer usosId);
}
