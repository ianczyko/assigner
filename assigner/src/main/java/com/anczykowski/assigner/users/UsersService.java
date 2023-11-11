package com.anczykowski.assigner.users;

import com.anczykowski.assigner.users.models.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class UsersService {

    UsersRepository usersRepository;

    @Transactional
    public User createOrGet(User user) {
        var existingUser = usersRepository.getByUsosId(user.getUsosId());
        return existingUser.orElseGet(() -> usersRepository.save(user));
    }
}
