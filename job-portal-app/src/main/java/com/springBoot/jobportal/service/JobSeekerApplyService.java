package com.springBoot.jobportal.service;

import com.springBoot.jobportal.entity.JobPostActivity;
import com.springBoot.jobportal.entity.JobSeekerApply;
import com.springBoot.jobportal.entity.JobSeekerProfile;
import com.springBoot.jobportal.repository.JobSeekerApplyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobSeekerApplyService {

    private final JobSeekerApplyRepository jobSeekerApplyRepository;

    @Autowired
    public JobSeekerApplyService(JobSeekerApplyRepository jobSeekerApplyRepository) {
        this.jobSeekerApplyRepository = jobSeekerApplyRepository;
    }


    public List<JobSeekerApply> getCandidatesJob(JobSeekerProfile userAccountId){
        return jobSeekerApplyRepository.findByUserId(userAccountId);

    }
    public List<JobSeekerApply> getJobCandidates(JobPostActivity job){
        return jobSeekerApplyRepository.findByJob(job);
    }

    public void addNew(JobSeekerApply jobSeekerApply) {
        jobSeekerApplyRepository.save(jobSeekerApply);

    }
    public void deleteByJob(JobPostActivity job) {
        jobSeekerApplyRepository.deleteAllByJob(job);
    }
}
