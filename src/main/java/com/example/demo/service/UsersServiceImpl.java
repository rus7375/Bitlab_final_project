package com.example.demo.service;

import com.example.demo.entity.Roles;
import com.example.demo.entity.Users;
import com.example.demo.repo.RolesRepository;
import com.example.demo.repo.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UsersServiceImpl implements UsersService {
    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @Override
    public List<Users> getAllUsers() {
        return usersRepository.findAll();
    }

    @Override
    public Users getUser(String email) {
        return usersRepository.findByEmail(email);
    }

    @Override
    public Users addUser(Users user) {
        Roles role = rolesRepository.findById(3L).orElse(null);
        Set<Roles> userRoles = new HashSet<>();
        userRoles.add(role);

        user.setRoles(userRoles);
        return usersRepository.save(user);
    }

    @Override
    public Users updateUser(Users user) {
        return usersRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        usersRepository.deleteById(id);
    }

    @Override
    public List<Roles> getRoles() {
        return rolesRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Users user = usersRepository.findByEmail(s);
        if (user != null) {
            return user;
        } else {
            throw new UsernameNotFoundException("User Not Found");
        }
    }
}
