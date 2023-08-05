package com.anczykowski.assigner.users;

import com.anczykowski.assigner.users.models.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UsersService {

    UsersRepository usersRepository;

    public User create(User user) {
        return usersRepository.save(user);
    }
}
