package com.springBoot.jobportal.service;

import com.springBoot.jobportal.entity.JobSeekerProfile;
import com.springBoot.jobportal.entity.RecruiterProfile;
import com.springBoot.jobportal.entity.User;
import com.springBoot.jobportal.repository.JobSeekerProfileRepository;
import com.springBoot.jobportal.repository.RecruiterProfileRepository;
import com.springBoot.jobportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private  final RecruiterProfileRepository recruiterProfileRepository;
    private final JobSeekerProfileRepository jobSeekerProfileRepository;

    //it will inject password encoder that we configure it in security configuration
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RecruiterProfileRepository recruiterProfileRepository, JobSeekerProfileRepository jobSeekerProfileRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.recruiterProfileRepository = recruiterProfileRepository;
        this.jobSeekerProfileRepository = jobSeekerProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User addUser(User user){
        user.setActive(true);
        user.setRegistrationDate(new Date(System.currentTimeMillis()));

        int userTypeId = user.getUserTypeId().getUserTypeId();

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser =  userRepository.save(user);
        if(userTypeId == 1){
           recruiterProfileRepository.save(new RecruiterProfile(user));
        }else{
            jobSeekerProfileRepository.save(new JobSeekerProfile(user));
        }

        return savedUser;
    }

    public Optional<User> getUserByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public Object getCurrentUserProfile(){
       Authentication authentication= SecurityContextHolder.getContext().getAuthentication();

       if(!(authentication instanceof AnonymousAuthenticationToken)){
           String username = authentication.getName();
           User user = userRepository.findByEmail(username).orElseThrow(()->
                   new UsernameNotFoundException("Could not found user."));

           int userId = user.getUserId();
           if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("Recruiter"))){
              RecruiterProfile recruiterProfile= recruiterProfileRepository.findById(userId)
                      .orElse(new RecruiterProfile());
              return recruiterProfile;
           } else{
               JobSeekerProfile jobSeekerProfile = jobSeekerProfileRepository.findById(userId)
                       .orElse(new JobSeekerProfile());

               return  jobSeekerProfile;
           }
       }
        return null;
    }
}
