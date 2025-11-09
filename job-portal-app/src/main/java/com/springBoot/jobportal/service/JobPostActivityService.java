package com.springBoot.jobportal.service;

import com.springBoot.jobportal.DTO.IRecruiterJobs;
import com.springBoot.jobportal.DTO.RecruiterJobsDTO;
import com.springBoot.jobportal.entity.JobCompany;
import com.springBoot.jobportal.entity.JobLocation;
import com.springBoot.jobportal.entity.JobPostActivity;
import com.springBoot.jobportal.entity.RecruiterProfile;
import com.springBoot.jobportal.repository.JobPostActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class JobPostActivityService {

    private final JobPostActivityRepository jobPostActivityRepository;

    @Autowired
    public JobPostActivityService(JobPostActivityRepository jobPostActivityRepository) {
        this.jobPostActivityRepository = jobPostActivityRepository;
    }

    public JobPostActivity addNew(JobPostActivity jobPostActivity){
        return  jobPostActivityRepository.save(jobPostActivity);
    }

    public List<RecruiterJobsDTO> getRecruiterJobs(int recruiter){

        List<IRecruiterJobs> recruiterJobsDtos = jobPostActivityRepository.getRecruiterJobs(recruiter);

        List<RecruiterJobsDTO> recruiterJobsDTOList = new ArrayList<>();

        //convert info from db into DTO
        for (IRecruiterJobs rec: recruiterJobsDtos){
            JobLocation loc = new JobLocation(rec.getLocationId(),rec.getState(),rec.getCountry()
            ,rec.getCity());

            JobCompany comp = new JobCompany(rec.getName(),"",rec.getCompanyId());

            recruiterJobsDTOList.add( new RecruiterJobsDTO(rec.getTotalCandidates(),rec.getJob_postId(),rec.getJob_title(),
                    loc,comp));

        }
        return recruiterJobsDTOList;
    }

    public JobPostActivity getOne(Integer id) {
        return  jobPostActivityRepository.findById(id).orElseThrow(()->new
                RuntimeException("Job not found."));
    }

    public List<JobPostActivity> getAll() {
        return jobPostActivityRepository.findAll();
    }


    public List<JobPostActivity> search(String job, String location, List<String> type, List<String> remote, LocalDate searchDate) {
        return Objects.isNull(searchDate)?jobPostActivityRepository.searchWithoutDate(job,location,remote,type):
                jobPostActivityRepository.search(job,location,remote,type,searchDate);
    }

    public void deleteById(int id) {
        jobPostActivityRepository.deleteById(id);
    }
}
