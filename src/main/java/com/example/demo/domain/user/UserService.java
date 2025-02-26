package com.example.demo.domain.user;

import com.example.demo.core.generic.AbstractService;
import com.example.demo.domain.user.dto.UserIdDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Set;

public interface UserService extends UserDetailsService, AbstractService<User> {
    User register(User user);

    User registerUser(User user);
    
    Set<User> getUsersById(Set<UserIdDTO> id);
}


