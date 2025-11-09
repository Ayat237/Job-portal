package com.springBoot.jobportal.controller;

import com.springBoot.jobportal.entity.*;
import com.springBoot.jobportal.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
public class JobSeekerApplyController {
    private final UserService userService;
    private final JobPostActivityService jobPostActivityService;
    private final JobSeekerApplyService jobSeekerApplyService;
    private final JobSeekerSaveService jobSeekerSaveService;
    private final RecruiterProfileService recruiterProfileService;
    private final JobSeekerProfileService jobSeekerProfileService;


    @Autowired
    public JobSeekerApplyController(UserService userService, JobPostActivityService jobPostActivityService, JobSeekerApplyService jobSeekerApplyService, JobSeekerSaveService jobSeekerSaveService, RecruiterProfileService recruiterProfileService, JobSeekerProfileService jobSeekerProfileService) {
        this.userService = userService;
        this.jobPostActivityService = jobPostActivityService;
        this.jobSeekerApplyService = jobSeekerApplyService;
        this.jobSeekerSaveService = jobSeekerSaveService;
        this.recruiterProfileService = recruiterProfileService;
        this.jobSeekerProfileService = jobSeekerProfileService;
    }


    @GetMapping("/job-details-apply/{id}")
    public String displayDetails(@PathVariable("id")int id, Model model){

        JobPostActivity jobDetails = jobPostActivityService.getOne(id);

        List<JobSeekerApply> jobSeekerApplyList = jobSeekerApplyService.getJobCandidates(jobDetails);
        List<JobSeekerSave> jobSeekerSaveList = jobSeekerSaveService.getJobCandidates(jobDetails);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("Recruiter"))){
                RecruiterProfile user  = recruiterProfileService.getCurrentRecruiterProfile();
                if (user != null){
                    model.addAttribute("applyList",jobSeekerApplyList);
                }
            }else{
                JobSeekerProfile user = jobSeekerProfileService.getCurrentjobSeekerProfile();
                if (user != null){
                    boolean exist = false;
                    boolean saved = false;
                    for (JobSeekerApply jobSeekerApply : jobSeekerApplyList){
                       if (jobSeekerApply.getUserId().getUserAccountId() == user.getUserAccountId()){
                           exist = true;
                           break;
                       }
                    }
                    for (JobSeekerSave jobSeekerSave : jobSeekerSaveList){
                       if (jobSeekerSave.getUserId().getUserAccountId()==user.getUserAccountId()){
                           saved = true;
                           break;
                       }
                    }
                    model.addAttribute("alreadyApplied",exist);
                    model.addAttribute("alreadySaved",saved);
                }

            }
        }

        JobSeekerApply jobSeekerApply = new JobSeekerApply();
        model.addAttribute("applyJob",jobSeekerApply);
        model.addAttribute("jobDetails",jobDetails);

        Object user = userService.getCurrentUserProfile();
        model.addAttribute("user",user);

        return "job-details";
    }

    @PostMapping("job-details/apply/{id}")
    public String apply(@PathVariable("id") int id, JobSeekerApply jobSeekerApply){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String username = authentication.getName();

            User user = userService.findByEmail(username);
            Optional<JobSeekerProfile> jobSeekerProfile = jobSeekerProfileService.getOne(user.getUserId());
            JobPostActivity jobPostActivity = jobPostActivityService.getOne(id);
            if (jobSeekerProfile.isPresent()&& jobPostActivity != null){
                jobSeekerApply = new JobSeekerApply();
                jobSeekerApply.setUserId(jobSeekerProfile.get());
                jobSeekerApply.setJob(jobPostActivity);
                jobSeekerApply.setApplyDate(new Date());
            }else{
                throw new RuntimeException("User not found.");
            }
        jobSeekerApplyService.addNew(jobSeekerApply);
        }
        return "redirect:/dashboard/";
    }
}
