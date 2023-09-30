package com.anczykowski.assigner.users.persistent;

import com.anczykowski.assigner.teams.models.Team;
import com.anczykowski.assigner.users.UsersRepository;
import com.anczykowski.assigner.users.models.User;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class UsersRepositoryPersistent implements UsersRepository {

    UsersRepositoryPersistentImpl repositoryImpl;

    ModelMapper modelMapper;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public User save(User user) {
        var userPersistent = modelMapper.map(user, UserPersistent.class);
        var userPersistentSaved = repositoryImpl.save(userPersistent);
        return modelMapper.map(userPersistentSaved, User.class);
    }

    @Override
    public User get(Integer id) {
        return modelMapper.map(repositoryImpl.getReferenceById(id), User.class);
    }

    @Override
    public Optional<User> getByUsosId(Integer usosId) {
        return repositoryImpl.findByUsosId(usosId)
                .map(ce -> modelMapper.map(ce, User.class));
    }

    @Override
    public List<Team> getAssignedTeamByUsosId(Integer usosId) {
        return repositoryImpl.findByUsosId(usosId)
                .map(user -> user.getTeamAccesses().stream()
                        .map(ta -> modelMapper.map(ta, Team.class))
                        .collect(Collectors.toList())
                ).orElse(new ArrayList<>());
    }
}

@Component
interface UsersRepositoryPersistentImpl extends JpaRepository<UserPersistent, Integer> {
    Optional<UserPersistent> findByUsosId(Integer usosId);
}