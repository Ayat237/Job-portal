package com.springBoot.jobportal.repository;

import com.springBoot.jobportal.entity.JobPostActivity;
import com.springBoot.jobportal.entity.JobSeekerProfile;
import com.springBoot.jobportal.entity.JobSeekerSave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobSeekerSaveRepository extends JpaRepository<JobSeekerSave,Integer> {
    public List<JobSeekerSave> findByUserId(JobSeekerProfile userId);

    public List<JobSeekerSave> findByJob(JobPostActivity job);
}
