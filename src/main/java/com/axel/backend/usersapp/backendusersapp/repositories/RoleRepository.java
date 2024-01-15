package com.axel.backend.usersapp.backendusersapp.repositories;

import com.axel.backend.usersapp.backendusersapp.models.entities.Role;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RoleRepository extends CrudRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
