package com.anczykowski.assigner.users;

import com.anczykowski.assigner.error.NotFoundException;
import com.anczykowski.assigner.users.models.User;
import com.anczykowski.assigner.users.models.UserType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public List<User> getAll() {
        return usersRepository.getAll();
    }

    @Transactional
    public User changeRole(Integer usosId, Integer newRole) {
        var user = usersRepository.getByUsosId(usosId)
            .orElseThrow(() -> new NotFoundException("user with usosId %d not found".formatted(usosId)));
        var userType = UserType.values()[newRole];
        user.setUserType(userType);
        return usersRepository.save(user);
    }
}
