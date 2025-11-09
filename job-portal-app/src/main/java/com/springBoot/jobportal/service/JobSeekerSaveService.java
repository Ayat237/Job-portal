package com.springBoot.jobportal.service;

import com.springBoot.jobportal.entity.JobPostActivity;
import com.springBoot.jobportal.entity.JobSeekerProfile;
import com.springBoot.jobportal.entity.JobSeekerSave;
import com.springBoot.jobportal.repository.JobSeekerSaveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JobSeekerSaveService {
    private final JobSeekerSaveRepository jobSeekerSaveRepository;

    @Autowired
    public JobSeekerSaveService(JobSeekerSaveRepository jobSeekerSaveRepository) {
        this.jobSeekerSaveRepository = jobSeekerSaveRepository;
    }


    public List<JobSeekerSave> getCandidatesJob(JobSeekerProfile userAccountId){
        return jobSeekerSaveRepository.findByUserId(userAccountId);
    }

    public List<JobSeekerSave> getJobCandidates(JobPostActivity job){
        return jobSeekerSaveRepository.findByJob(job);
    }

    public void addNew(JobSeekerSave jobSeekerSave) {
        jobSeekerSaveRepository.save(jobSeekerSave);
    }
}
