package com.springBoot.jobportal.controller;

import ch.qos.logback.core.util.StringUtil;
import com.springBoot.jobportal.entity.RecruiterProfile;
import com.springBoot.jobportal.entity.User;
import com.springBoot.jobportal.repository.UserRepository;
import com.springBoot.jobportal.service.RecruiterProfileService;
import com.springBoot.jobportal.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Struct;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/recruiter-profile")
public class RecruiterProfileController {
    private final UserRepository userRepository;
    private final RecruiterProfileService recruiterProfileService;

    @Autowired
    public RecruiterProfileController(UserRepository userRepository, RecruiterProfileService recruiterProfileService) {
        this.userRepository = userRepository;
        this.recruiterProfileService = recruiterProfileService;
    }

    @GetMapping("/")
    public String recruiterProfile(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)){

            String currentUsername = authentication.getName();

            User user = userRepository.findByEmail(currentUsername).orElseThrow(()->
                    new UsernameNotFoundException("could not found user."));

            Optional<RecruiterProfile> recruiterProfile = recruiterProfileService.
                    getOne(user.getUserId());

            if (recruiterProfile.isPresent()){
                model.addAttribute("profile",recruiterProfile.get());
            }

        }
        return "recruiter_profile";
    }

    @PostMapping("/addNew")
    public String addNew(RecruiterProfile recruiterProfile ,
                         @RequestParam("image")MultipartFile multipartFile,Model model){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)) {

            String currentUsername = authentication.getName();

            User user = userRepository.findByEmail(currentUsername).orElseThrow(() ->
                    new UsernameNotFoundException("could not found user."));

            //associate a recruiter profile with existing userAccount
            recruiterProfile.setUserId(user);
            recruiterProfile.setUserAccountId(user.getUserId());
        }
        model.addAttribute("profile",recruiterProfile);

        //handle profile image
        String fileName="";
        if(!multipartFile.getOriginalFilename().equals("")){
            fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            recruiterProfile.setProfilePhoto(fileName);
        }

        RecruiterProfile savedUser = recruiterProfileService.addNew(recruiterProfile);

        //initialize the directory for uploaded images

        String uploadDir = "photos/recruiter/"+savedUser.getUserAccountId();

        try {
            FileUploadUtil.saveFile(uploadDir,fileName,multipartFile);
        }catch (Exception exc){
            exc.fillInStackTrace();
        }
        return "redirect:/dashboard/";
    }
}
