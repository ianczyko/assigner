package com.anczykowski.assigner.users.persistent;

import com.anczykowski.assigner.users.UsersRepository;
import com.anczykowski.assigner.users.models.User;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@AllArgsConstructor
public class UsersRepositoryPersistent implements UsersRepository {

    UsersRepositoryPersistentImpl repositoryImpl;

    ModelMapper modelMapper;

    @Override
    @Transactional
    public User save(User user) {
        var userPersistent = modelMapper.map(user, UserPersistent.class);
        var userPersistentSaved = repositoryImpl.save(userPersistent);
        return modelMapper.map(userPersistentSaved, User.class);
    }
}

@Component
interface UsersRepositoryPersistentImpl extends JpaRepository<UserPersistent, Integer> {
}