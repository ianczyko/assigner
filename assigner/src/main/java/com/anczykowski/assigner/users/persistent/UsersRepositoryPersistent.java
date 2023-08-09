package com.anczykowski.assigner.users.persistent;

import com.anczykowski.assigner.users.UsersRepository;
import com.anczykowski.assigner.users.models.User;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Override
    @Transactional
    public User get(Integer id) {
        return modelMapper.map(repositoryImpl.getReferenceById(id), User.class);
    }

    @Override
    @Transactional
    public User getByUsosId(Integer usosId) {
        return modelMapper.map(repositoryImpl.findByUsosId(usosId).get(0), User.class);
    }
}

@Component
interface UsersRepositoryPersistentImpl extends JpaRepository<UserPersistent, Integer> {
    List<UserPersistent> findByUsosId(Integer usosId);
}