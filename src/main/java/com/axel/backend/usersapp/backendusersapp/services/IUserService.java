package com.axel.backend.usersapp.backendusersapp.services;

import com.axel.backend.usersapp.backendusersapp.models.dto.UserDto;
import com.axel.backend.usersapp.backendusersapp.models.entities.User;
import com.axel.backend.usersapp.backendusersapp.models.request.UserRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    List<UserDto> findAll();
    Optional<UserDto> findById(Long id);
    UserDto save(User user);
    Optional<UserDto> update(UserRequest user, Long id);
    void remove(Long id);
    Page<UserDto> findAll(Pageable pageable);
}
