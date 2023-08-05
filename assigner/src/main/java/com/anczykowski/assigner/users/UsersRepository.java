package com.anczykowski.assigner.users;

import com.anczykowski.assigner.users.models.User;

public interface UsersRepository {
    User save(User user);
}
