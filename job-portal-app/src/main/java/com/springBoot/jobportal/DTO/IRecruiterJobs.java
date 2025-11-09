package com.springBoot.jobportal.DTO;

public interface IRecruiterJobs {

    Long getTotalCandidates();

    int getJob_postId();

    String getJob_title();

    int getLocationId();

    String getCity();

    String getCountry();

    String getState();

    int getCompanyId();

    String getName();

}
