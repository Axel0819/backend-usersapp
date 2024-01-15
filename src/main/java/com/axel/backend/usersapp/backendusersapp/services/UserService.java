package com.axel.backend.usersapp.backendusersapp.services;

import com.axel.backend.usersapp.backendusersapp.interfaces.IUser;
import com.axel.backend.usersapp.backendusersapp.models.dto.UserDto;
import com.axel.backend.usersapp.backendusersapp.models.dto.mapper.DtoMapperUser;
import com.axel.backend.usersapp.backendusersapp.models.entities.Role;
import com.axel.backend.usersapp.backendusersapp.models.entities.User;
import com.axel.backend.usersapp.backendusersapp.models.request.UserRequest;
import com.axel.backend.usersapp.backendusersapp.repositories.RoleRepository;
import com.axel.backend.usersapp.backendusersapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements IUserService {
    @Autowired
    private UserRepository repository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
       List<User> users = (List<User>) repository.findAll();
       return users.stream()
               .map(user-> DtoMapperUser.builder().setUser(user).build())
               .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDto> findById(Long id) {
        return repository.findById(id).map(user-> DtoMapperUser.builder().setUser(user).build());
    }

    @Override
    @Transactional
    public UserDto save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(getRoles(user));
        return DtoMapperUser.builder().setUser(repository.save(user)).build();
    }

    @Override
    @Transactional
    public Optional<UserDto> update(UserRequest user, Long id) {
        Optional<User> userOptional = repository.findById(id);
        User userOp = null;

        if (userOptional.isPresent()) {
            User userDb = userOptional.orElseThrow();
            userDb.setRoles(getRoles(user));
            userDb.setUsername(user.getUsername());
            userDb.setEmail(user.getEmail());
            userOp = repository.save(userDb);
        }

        return Optional.ofNullable(DtoMapperUser.builder().setUser(userOp).build());
    }
    @Override
    @Transactional
    public void remove(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> findAll(Pageable pageable) {
        return repository
                .findAll(pageable)
                .map(u -> DtoMapperUser.builder().setUser(u).build());
    }

    private List<Role> getRoles(IUser user){
        Optional<Role> opUserRole = roleRepository.findByName("ROLE_USER");
        List<Role> roles = new ArrayList<>();

        if(opUserRole.isPresent()){
            roles.add(opUserRole.orElseThrow());
        }

        if(user.isAdmin()){
            Optional<Role> opAdminRole = roleRepository.findByName("ROLE_ADMIN");
            if(opAdminRole.isPresent()){
                roles.add(opAdminRole.orElseThrow());
            }
        }
        return roles;
    }

}
