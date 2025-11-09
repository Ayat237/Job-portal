package com.springBoot.jobportal.controller;

import com.springBoot.jobportal.entity.JobPostActivity;
import com.springBoot.jobportal.entity.JobSeekerProfile;
import com.springBoot.jobportal.entity.JobSeekerSave;
import com.springBoot.jobportal.entity.User;
import com.springBoot.jobportal.service.JobPostActivityService;
import com.springBoot.jobportal.service.JobSeekerProfileService;
import com.springBoot.jobportal.service.JobSeekerSaveService;
import com.springBoot.jobportal.service.UserService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class JobSeekerSaveController {

    private  final UserService userService;
    private final JobSeekerSaveService jobSeekerSaveService;
    private final JobSeekerProfileService jobSeekerProfileService;
    private final JobPostActivityService jobPostActivityService;

    public JobSeekerSaveController(UserService userService, JobSeekerSaveService jobSeekerSaveService, JobSeekerProfileService jobSeekerProfileService, JobPostActivityService jobPostActivityService) {
        this.userService = userService;
        this.jobSeekerSaveService = jobSeekerSaveService;
        this.jobSeekerProfileService = jobSeekerProfileService;
        this.jobPostActivityService = jobPostActivityService;
    }

    @PostMapping("job-details/save/{id}")
    public String save(@PathVariable("id") int id, JobSeekerSave jobSeekerSave){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)) {

            String currentUsername = authentication.getName();
            User user = userService.findByEmail(currentUsername);

            Optional<JobSeekerProfile> jobSeekerProfile = jobSeekerProfileService.getOne(user.getUserId());
            JobPostActivity jobPostActivity = jobPostActivityService.getOne(id);

            if (jobSeekerProfile.isPresent() && jobPostActivity !=null){
                jobSeekerSave = new JobSeekerSave();
                jobSeekerSave.setJob(jobPostActivity);
                jobSeekerSave.setUserId(jobSeekerProfile.get());

            }else {
                throw new RuntimeException("user not found.");
            }
            jobSeekerSaveService.addNew(jobSeekerSave);

        }

        return "redirect:/dashboard/";
    }

    //show list of saved method
    @GetMapping("saved-jobs/")
    public String savedJobs(Model model){
        List<JobPostActivity> jobPostActivities = new ArrayList<>();

        Object currentUserProfile = userService.getCurrentUserProfile();

        List<JobSeekerSave> jobSeekerSaveList = jobSeekerSaveService.getCandidatesJob((JobSeekerProfile) currentUserProfile);

        for (JobSeekerSave jobSeekerSave : jobSeekerSaveList){
            jobPostActivities.add(jobSeekerSave.getJob());
        }
        model.addAttribute("jobPost",jobPostActivities);
        model.addAttribute("user",currentUserProfile);
        return "saved-jobs";
    }
}
