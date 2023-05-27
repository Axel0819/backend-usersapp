package com.axel.backend.usersapp.backendusersapp.services;

import com.axel.backend.usersapp.backendusersapp.models.entities.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    List<User> findAll();
    Optional<User> findById(Long id);
    User save(User user);
    Optional<User> update(User user, Long id);
    void remove(Long id);
}
