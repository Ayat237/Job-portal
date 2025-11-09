package com.springBoot.jobportal.repository;

import com.springBoot.jobportal.entity.JobPostActivity;
import com.springBoot.jobportal.entity.JobSeekerApply;
import com.springBoot.jobportal.entity.JobSeekerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobSeekerApplyRepository extends JpaRepository<JobSeekerApply,Integer> {

   public List<JobSeekerApply> findByUserId(JobSeekerProfile userId);

   public List<JobSeekerApply> findByJob(JobPostActivity job);

   void deleteAllByJob(JobPostActivity job);

}
