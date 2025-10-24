package com.springBoot.jobportal.repository;

import com.springBoot.jobportal.entity.JobSeekerProfile;
import com.springBoot.jobportal.entity.RecruiterProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobSeekerProfileRepository extends JpaRepository<JobSeekerProfile, Integer> {
}
