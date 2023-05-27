package com.axel.backend.usersapp.backendusersapp.services;

import com.axel.backend.usersapp.backendusersapp.models.entities.User;
import com.axel.backend.usersapp.backendusersapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService {
    @Autowired
    private UserRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return (List<User>) repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public User save(User user) {
        return repository.save(user);
    }

    @Override
    @Transactional
    public Optional<User> update(User user, Long id) {
        Optional<User> userOptional = this.findById(id);
        User userOp = null;

        if (userOptional.isPresent()) {
            User userDb = userOptional.orElseThrow();

            userDb.setUsername(user.getUsername());
            userDb.setEmail(user.getEmail());

            userOp = this.save(userDb);
        }

        return Optional.ofNullable(userOp);
    }
    @Override
    @Transactional
    public void remove(Long id) {
        repository.deleteById(id);
    }
}
