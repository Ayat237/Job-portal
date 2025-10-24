package com.springBoot.jobportal.controller;


import com.springBoot.jobportal.entity.User;
import com.springBoot.jobportal.entity.UserType;
import com.springBoot.jobportal.service.UserService;
import com.springBoot.jobportal.service.UserTypeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Controller
public class UserController {
    private final UserTypeService userTypeService;
    private final UserService userService;

    @Autowired
    public UserController(UserTypeService userTypeService, UserService userService) {
        this.userTypeService = userTypeService;
        this.userService = userService;
    }

    @GetMapping("/register")
    public String register(Model model){
        List<UserType> userTypes = userTypeService.getAll();

        model.addAttribute("getAllTypes",userTypes);
        model.addAttribute("user",new User());

        return "register";
    }

    @PostMapping("/register/new")
    public String userRegistration(@Valid User user,Model model){

        Optional<User>  optionalUser = userService.getUserByEmail(user.getEmail());

        if (optionalUser.isPresent()){
            model.addAttribute("error","Email already registered, " +
                    "try to login or register with other email.");

            List<UserType> userTypes = userTypeService.getAll();

            model.addAttribute("getAllTypes",userTypes);
            model.addAttribute("user",new User());

            return "register";
        }

        userService.addUser(user);
        return "dashboard";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response){
        Authentication authentication  = SecurityContextHolder.getContext().getAuthentication();

        if(authentication != null){
            new SecurityContextLogoutHandler().logout(request,response,authentication);
        }

        return "redirect:/";
    }
}
