package com.example.demo.controller;

import com.example.demo.entity.Users;
import com.example.demo.service.UsersService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@PreAuthorize("isAuthenticated()")
@RequestMapping(value = "/user")
public class UsersController {

    @Autowired
    private UsersService usersService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${file.avatar.viewPath}")
    private String viewPath;

    @Value("${file.avatar.uploadPath}")
    private String uploadPath;

    @Value("${file.avatar.defaultPicture}")
    private String defaultPicture;

    @PreAuthorize("isAnonymous()")
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/toregister")
    public String toRegister(@RequestParam(name = "email") String email,
                             @RequestParam(name = "password") String password,
                             @RequestParam(name = "re_password") String rePassword,
                             @RequestParam(name = "name") String name,
                             @RequestParam(name = "surname") String surname,
                             @RequestParam(name = "gender") String gender,
                             @RequestParam(name = "birth_date") String birthDate,
                             @RequestParam(name = "phone_number") String phoneNumber) {

        String redirect = "?emailerror";
        Users user = usersService.getUser(email);
        if(user == null) {
            redirect = "?passworderror";
            if(password.equals(rePassword)) {
                redirect = "/login";
                user = new Users();
                user.setEmail(email);
                user.setPassword(passwordEncoder.encode(password));
                user.setName(name);
                user.setSurname(surname);
                user.setGender(gender);
                user.setBirthDate(birthDate);
                user.setPhoneNumber(phoneNumber);

                usersService.addUser(user);
            }

        }
        return "redirect:/user" + redirect;
    }

    @GetMapping("/profile")
    public String profile(Model model){
        model.addAttribute("CURRENT_USER", getUser());
        return "profile";
    }

    @GetMapping("/edit-profile")
    public String profileDetails(Model model){
        model.addAttribute("CURRENT_USER", getUser());
        return "profile_edit";
    }

    @PostMapping("edit-profile")
    public String editProfile(@RequestParam(name = "name") String name,
                              @RequestParam(name = "surname") String surname,
                              @RequestParam(name = "gender") String gender,
                              @RequestParam(name = "birth_date") String birthDate,
                              @RequestParam(name = "phone_number") String phoneNumber) {
        Users currentUser = getUser();

        currentUser.setName(name);
        currentUser.setSurname(surname);
        currentUser.setGender(gender);
        currentUser.setBirthDate(birthDate);
        currentUser.setPhoneNumber(phoneNumber);

        usersService.updateUser(currentUser);

        return "redirect:/user/edit-profile?success";
    }


    @GetMapping("/password")
    public String password(Model model){
        model.addAttribute("CURRENT_USER", getUser());
        return "user_password";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam(name = "old_password") String oldPassword,
                                 @RequestParam(name = "password") String password,
                                 @RequestParam(name = "re_password") String rePassword) {
        Users currentUser = getUser();
        String redirect = "repassworderror";
        if(password.equals(rePassword)) {
            redirect = "oldpassworderror";
            if(passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
                redirect = "success";
                currentUser.setPassword(passwordEncoder.encode(password));
                usersService.updateUser(currentUser);
            }
        }
        return "redirect:/user/password?" + redirect;
    }

    @PostMapping(value = "/upload-avatar")
    public String uploadAvatar(@RequestParam(name = "avatar") MultipartFile file) {
        if(file.getContentType().equals("image/jpeg") || file.getContentType().equals("image/png")) {

            try{
                Users user = getUser();

                String picName = DigestUtils.sha1Hex("avatar_" + user.getId() + "_!Picture");
                user.setAvatar(picName);

                byte[] bytes = file.getBytes();
                Path path = Paths.get(uploadPath + picName + ".jpg");
                Files.write(path, bytes);

                usersService.updateUser(user);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return "redirect:/user/profile?avatarsuccess";
        }
        return "redirect:/user/profile?error";
    }

    @GetMapping(value = "view-photo/{url}", produces = {MediaType.IMAGE_JPEG_VALUE})
    public @ResponseBody byte[] viewProfilePhoto(@PathVariable(name = "url") String url) throws IOException {
        String pictureURL = viewPath + defaultPicture;
        if(url != null && !url.equals("null")) {
            pictureURL = viewPath + url + ".jpg";
        }

        InputStream inputStream;

        try{
            ClassPathResource resource = new ClassPathResource(pictureURL);
            inputStream = resource.getInputStream();
        } catch (Exception e) {
            ClassPathResource resource = new ClassPathResource(viewPath + defaultPicture);
            inputStream = resource.getInputStream();
            e.printStackTrace();
        }
        return IOUtils.toByteArray(inputStream);
    }




   private Users getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)) {
            return (Users) authentication.getPrincipal();
        }
        return null;
   }

}
