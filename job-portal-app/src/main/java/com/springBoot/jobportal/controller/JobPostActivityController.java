package com.springBoot.jobportal.controller;

import com.springBoot.jobportal.DTO.RecruiterJobsDTO;
import com.springBoot.jobportal.entity.*;
import com.springBoot.jobportal.service.JobPostActivityService;
import com.springBoot.jobportal.service.JobSeekerApplyService;
import com.springBoot.jobportal.service.JobSeekerSaveService;
import com.springBoot.jobportal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Controller

public class JobPostActivityController {

    private final UserService userService;
    private final JobPostActivityService jobPostActivityService;
    private  final JobSeekerApplyService jobSeekerApplyService;
    private final JobSeekerSaveService jobSeekerSaveService;


    @Autowired
    public JobPostActivityController(UserService userService, JobPostActivityService jobPostActivityService, JobSeekerApplyService jobSeekerApplyService, JobSeekerSaveService jobSeekerSaveService) {
        this.userService = userService;
        this.jobPostActivityService = jobPostActivityService;
        this.jobSeekerApplyService = jobSeekerApplyService;
        this.jobSeekerSaveService = jobSeekerSaveService;
    }

    @GetMapping("/dashboard/")
    public String searchJobs(Model model, @RequestParam(value = "job",required = false) String job,
                             @RequestParam(value = "location",required = false) String location,
                             @RequestParam(value = "partTime",required = false) String partTime,
                             @RequestParam(value = "fullTime",required = false) String fullTime,
                             @RequestParam(value = "freelance",required = false) String freelance,
                             @RequestParam(value = "remoteOnly",required = false) String remoteOnly,
                             @RequestParam(value = "officeOnly",required = false) String officeOnly,
                             @RequestParam(value = "partialRemote",required = false) String partialRemote,
                             @RequestParam(value = "today",required = false) boolean today,
                             @RequestParam(value = "days7",required = false) boolean days7,
                             @RequestParam(value = "days30",required = false) boolean days30){

        model.addAttribute("partTime", Objects.equals(partTime,"Part-Time"));
        model.addAttribute("fullTime", Objects.equals(fullTime,"Full-Time"));
        model.addAttribute("freelance", Objects.equals(freelance,"Freelance"));

        model.addAttribute("remoteOnly", Objects.equals(remoteOnly,"Remote-Only"));
        model.addAttribute("officeOnly", Objects.equals(officeOnly,"Office-Only"));
        model.addAttribute("partialRemote", Objects.equals(partialRemote,"Partial-Remote"));

        model.addAttribute("today",today);
        model.addAttribute("days7",days7);
        model.addAttribute("days30", days30);

        model.addAttribute("job",job);
        model.addAttribute("location",location);

        LocalDate searchDate = null;
        List<JobPostActivity> jobPosts = null;

        Boolean dateSearchFlag = true;
        Boolean remote = true;
        Boolean type = true;

        if (days30){
            searchDate = LocalDate.now().minusDays(30);
        }else if (days7){
            searchDate = LocalDate.now().minusDays(7);
        }else if (today){
            searchDate = LocalDate.now();
        }else {
            dateSearchFlag = false;
        }

        if (partTime == null && fullTime == null && freelance == null){
            partTime = "Part-Time";
            fullTime = "Full-Time";
            freelance = "Freelance";
            remote = false;

        }

        if (officeOnly == null && remoteOnly == null && partialRemote == null){
            officeOnly = "Office-Only";
            remoteOnly = "Remote-Only";
            partialRemote = "Partial-Remote";
            type = false;

        }

        if(!dateSearchFlag && !remote && !type && !StringUtils.hasText(job) && ! StringUtils.hasText(location)){
            jobPosts = jobPostActivityService.getAll();
        }else {
            jobPosts = jobPostActivityService.search(job,location, Arrays.asList(partTime,fullTime,freelance),
                    Arrays.asList(remoteOnly,officeOnly,partialRemote),searchDate);
        }

        Object currentUserProfile = userService.getCurrentUserProfile();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(!(authentication instanceof AnonymousAuthenticationToken)){
            String username = authentication.getName();

            model.addAttribute("username",username);
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("Recruiter"))){

               List<RecruiterJobsDTO>  recruiterJobs = jobPostActivityService.getRecruiterJobs((
                       (RecruiterProfile)currentUserProfile).getUserAccountId());

               model.addAttribute("jobPost",recruiterJobs);
            }else{
                List<JobSeekerApply> jobSeekerApplyList =  jobSeekerApplyService.
                        getCandidatesJob((JobSeekerProfile)currentUserProfile);

                List<JobSeekerSave> jobSeekerSaveList =  jobSeekerSaveService.
                        getCandidatesJob((JobSeekerProfile)currentUserProfile);

                Boolean exist ;
                Boolean saved;

                for (JobPostActivity jobActivity: jobPosts){
                    exist = false;
                    saved = false;
                    for (JobSeekerApply jobApply : jobSeekerApplyList){
                        if(Objects.equals(jobActivity.getJobPostId(),jobApply.getJob().getJobPostId())){
                            jobActivity.setIsActive(true);
                            exist = true;
                            break;
                        }
                    }
                    for (JobSeekerSave jobSave : jobSeekerSaveList){
                        if(Objects.equals(jobActivity.getJobPostId(),jobSave.getJob().getJobPostId())){
                            jobActivity.setIsSaved(true);
                            saved = true;
                            break;
                        }
                    }

                    if (!exist){
                        jobActivity.setIsActive(false);
                    }
                    if (!saved){
                        jobActivity.setIsSaved(false);
                    }
                    model.addAttribute("jobPost",jobPosts);
                }

            }
        }

        model.addAttribute("user",currentUserProfile);

        return "dashboard";
    }

    @GetMapping("/dashboard/add")
    public String addJobs(Model model){

        model.addAttribute("jobPostActivity", new JobPostActivity());
        model.addAttribute("user",userService.getCurrentUserProfile());
        return "add-jobs";
    }

    @PostMapping("/dashboard/addNew")
    public String addNewJob(JobPostActivity jobPostActivity, Model model){

        User user = userService.getCurrentUser();
        if (user != null){
            jobPostActivity.setPostedById(user);
        }
        jobPostActivity.setPostedDate(new Date());

        model.addAttribute("jobPostActivity",jobPostActivity);

        JobPostActivity savedJob = jobPostActivityService.addNew(jobPostActivity);


        return "redirect:/dashboard/";
    }

    @PostMapping("/dashboard/edit/{id}")
    public String editJob(@PathVariable("id") int id,Model model){
        JobPostActivity jobPostActivity = jobPostActivityService.getOne(id);
        model.addAttribute("jobPostActivity",jobPostActivity);

        Object user = userService.getCurrentUserProfile();
        model.addAttribute("user",user);

        return "add-jobs";
    }

    @PostMapping("/dashboard/deleteJob/{id}")
    public String deleteJob(@PathVariable("id") int id, Model model) {

        JobPostActivity jobPostActivity = jobPostActivityService.getOne(id);
        Object user = userService.getCurrentUserProfile();
        if (jobPostActivity != null){
            jobSeekerApplyService.deleteByJob(jobPostActivity);
            jobPostActivityService.deleteById(id);
        }

        model.addAttribute("user",user);

        return "redirect:/dashboard/";
    }


    @GetMapping("global-search/")
    public String globalSearch(Model model,
                               @RequestParam(value = "job",required = false) String job,
                               @RequestParam(value = "location",required = false) String location,
                               @RequestParam(value = "partTime",required = false) String partTime,
                               @RequestParam(value = "fullTime",required = false) String fullTime,
                               @RequestParam(value = "freelance",required = false) String freelance,
                               @RequestParam(value = "remoteOnly",required = false) String remoteOnly,
                               @RequestParam(value = "officeOnly",required = false) String officeOnly,
                               @RequestParam(value = "partialRemote",required = false) String partialRemote,
                               @RequestParam(value = "today",required = false) boolean today,
                               @RequestParam(value = "days7",required = false) boolean days7,
                               @RequestParam(value = "days30",required = false) boolean days30){

        model.addAttribute("partTime", Objects.equals(partTime,"Part-Time"));
        model.addAttribute("fullTime", Objects.equals(fullTime,"Full-Time"));
        model.addAttribute("freelance", Objects.equals(freelance,"Freelance"));

        model.addAttribute("remoteOnly", Objects.equals(remoteOnly,"Remote-Only"));
        model.addAttribute("officeOnly", Objects.equals(officeOnly,"Office-Only"));
        model.addAttribute("partialRemote", Objects.equals(partialRemote,"Partial-Remote"));

        model.addAttribute("today",today);
        model.addAttribute("days7",days7);
        model.addAttribute("days30", days30);

        model.addAttribute("job",job);
        model.addAttribute("location",location);

        LocalDate searchDate = null;
        List<JobPostActivity> jobPosts = null;

        Boolean dateSearchFlag = true;
        Boolean remote = true;
        Boolean type = true;

        if (days30){
            searchDate = LocalDate.now().minusDays(30);
        }else if (days7){
            searchDate = LocalDate.now().minusDays(7);
        }else if (today){
            searchDate = LocalDate.now();
        }else {
            dateSearchFlag = false;
        }

        if (partTime == null && fullTime == null && freelance == null){
            partTime = "Part-Time";
            fullTime = "Full-Time";
            freelance = "Freelance";
            remote = false;

        }

        if (officeOnly == null && remoteOnly == null && partialRemote == null){
            officeOnly = "Office-Only";
            remoteOnly = "Remote-Only";
            partialRemote = "Partial-Remote";
            type = false;

        }

        if(!dateSearchFlag && !remote && !type && !StringUtils.hasText(job) && ! StringUtils.hasText(location)){
            jobPosts = jobPostActivityService.getAll();
        }else {
            jobPosts = jobPostActivityService.search(job,location, Arrays.asList(partTime,fullTime,freelance),
                    Arrays.asList(remoteOnly,officeOnly,partialRemote),searchDate);
        }

        model.addAttribute("jobPost",jobPosts);

        return "global-search";

    }
}
