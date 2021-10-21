package com.example.demo.service;

import com.example.demo.entity.Roles;
import com.example.demo.entity.Users;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UsersService extends UserDetailsService {

    List<Users> getAllUsers();
    Users getUser(String email);
    Users addUser(Users user);
    Users updateUser(Users user);
    void deleteUser(Long id);

    List<Roles> getRoles();

}
