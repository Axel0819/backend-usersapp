package com.axel.backend.usersapp.backendusersapp.models.dto.mapper;

import com.axel.backend.usersapp.backendusersapp.models.dto.UserDto;
import com.axel.backend.usersapp.backendusersapp.models.entities.User;

public class DtoMapperUser {
    private User user;
    private DtoMapperUser(){}
    public DtoMapperUser setUser(User user) {
        this.user = user;
        return this;
    }
    public static DtoMapperUser builder(){
       return new DtoMapperUser();
    }
    public UserDto build(){
        if(user == null) throw new RuntimeException("Debe pasar el entity user");
        boolean isAdmin = this.user.getRoles().stream().anyMatch(r-> "ROLE_ADMIN".equals(r.getName()));
        return new UserDto(this.user.getId(), this.user.getUsername(), this.user.getEmail(), isAdmin);
    }
}
