package com.springBoot.jobportal.controller;

import com.springBoot.jobportal.entity.JobSeekerProfile;
import com.springBoot.jobportal.entity.Skills;
import com.springBoot.jobportal.entity.User;
import com.springBoot.jobportal.repository.UserRepository;
import com.springBoot.jobportal.service.JobSeekerProfileService;
import com.springBoot.jobportal.service.UserService;
import com.springBoot.jobportal.util.FileDownloadUtil;
import com.springBoot.jobportal.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/job-seeker-profile")
public class JobSeekerProfileController {

    private final JobSeekerProfileService jobSeekerProfileService;
    private final UserRepository userRepository;


    @Autowired
    public JobSeekerProfileController(JobSeekerProfileService jobSeekerProfileService, UserRepository userRepository) {
        this.jobSeekerProfileService = jobSeekerProfileService;
        this.userRepository = userRepository;
    }


    @GetMapping("/")
    public String jobSeekerProfile(Model model){
        JobSeekerProfile jobSeekerProfile = new JobSeekerProfile();
        List<Skills> skills = new ArrayList<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)){
            String username = authentication.getName();

            User user = userRepository.findByEmail(username).orElseThrow(()->
                    new UsernameNotFoundException("Could not found user."));
            model.addAttribute("user",user);

            Optional<JobSeekerProfile> seekerProfile = jobSeekerProfileService.
                    getOne(user.getUserId());

            if(seekerProfile.isPresent()){
                jobSeekerProfile = seekerProfile.get();
                if(jobSeekerProfile.getSkills().isEmpty()){
                    skills.add(new Skills());
                    jobSeekerProfile.setSkills(skills);
                }
            }
            model.addAttribute("skills",skills);
            model.addAttribute("profile",jobSeekerProfile);

        }

        return "job-seeker-profile";
    }

    @PostMapping("/addNew")
    public String addNew(JobSeekerProfile jobSeekerProfile,
                         @RequestParam("image") MultipartFile image,
                         @RequestParam("pdf") MultipartFile pdf,
                         Model model){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)){
            String username = authentication.getName();

            User user = userRepository.findByEmail(username).orElseThrow(()->
                    new UsernameNotFoundException("Could not found user."));

            jobSeekerProfile.setUserId(user);
            jobSeekerProfile.setUserAccountId(user.getUserId());
        }

        List<Skills> skillsList= new ArrayList<>();
        model.addAttribute("skills",skillsList);
        model.addAttribute("profile",jobSeekerProfile);

        //associate the skills with jobseeker profile
        for (Skills skills : jobSeekerProfile.getSkills()){
            skills.setJobSeekerProfile(jobSeekerProfile);
        }

        String imageName="";
        String resumeName="";

        if(!image.isEmpty()){
            imageName = StringUtils.cleanPath(Objects.requireNonNull(image.getOriginalFilename()));
            jobSeekerProfile.setProfilePhoto(imageName);
        }

        if(!pdf.isEmpty()){
            resumeName = StringUtils.cleanPath(Objects.requireNonNull(pdf.getOriginalFilename()));
            jobSeekerProfile.setResume(resumeName);
        }

        JobSeekerProfile savedSeekerProfile = jobSeekerProfileService.addNew(jobSeekerProfile);

        //we should save the image and file to server system
        try {
            String uploadDir="photos/candidate/"+jobSeekerProfile.getUserAccountId();
            if(!image.isEmpty()){
                FileUploadUtil.saveFile(uploadDir,imageName,image);
            }
            if (!pdf.isEmpty()){
                FileUploadUtil.saveFile(uploadDir,resumeName,pdf);
            }

        }catch (IOException ex){
            throw  new RuntimeException("can no save either image or pdf",ex);
        }

        return "redirect:/dashboard/";
    }


    @GetMapping("/{id}")
    public String candidateProfile(@PathVariable("id") int id, Model model){
        Optional<JobSeekerProfile> jobSeekerProfile = jobSeekerProfileService.getOne(id);
        model.addAttribute("profile",jobSeekerProfile.get());

        return "job-seeker-profile";
    }

    @GetMapping("/downloadResume")
    public ResponseEntity<?> downloadResume(@RequestParam(value = "fileName")String fileName,
                                            @RequestParam(value = "userId")String userId){

        FileDownloadUtil fileDownloadUtil = new FileDownloadUtil();

        Resource resource = null;

        try {

            resource = fileDownloadUtil.getFileAsResource("photos/candidate/"+userId,fileName);
        }catch (IOException ex){
            return ResponseEntity.badRequest().build();
        }
        if (resource == null){
            return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
        }

        String contentType = "application/octet-stream";
        String headerValue = "attachment; filename=\""+resource.getFilename()+"\"";

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,headerValue)
                .body(resource);
    }
}
