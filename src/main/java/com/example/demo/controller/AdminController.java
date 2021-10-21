package com.example.demo.controller;

import com.example.demo.entity.Roles;
import com.example.demo.entity.Users;
import com.example.demo.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;

@Controller
@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
@RequestMapping("/admin-panel")
public class AdminController {

    @Autowired
    private UsersService usersService;


    @GetMapping("/roles")
    public String roles(Model model) {
        List<Users> users = usersService.getAllUsers();
        List<Roles> roles = usersService.getRoles();

        model.addAttribute("CURRENT_USER",  getUser());
        model.addAttribute("users", users);
        model.addAttribute("roles", roles);

        return "roles";
    }

    @PostMapping("/role")
    public String role(@RequestParam(name = "user_email") String email,
                       @RequestParam(name = "role_id") Integer roleId) {
        List<Roles> roles = usersService.getRoles();
        Users user = usersService.getUser(email);
        Roles role = roles.get(roleId);
        Set<Roles> userRoles = null;
        if(user!=null) {
            userRoles = user.getRoles();
            if(userRoles.contains(role)) {
                userRoles.remove(role);
            } else {
                userRoles.add(role);
            }
            user.setRoles(userRoles);
            usersService.updateUser(user);

            return "redirect:/admin-panel/roles?success";
        }
        return "redirect:/admin-panel/roles?error";
    }



    public Users getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)) {
            return (Users) authentication.getPrincipal();
        }

        return null;
    }
}
