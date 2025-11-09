package com.springBoot.jobportal.service;

import com.springBoot.jobportal.entity.RecruiterProfile;
import com.springBoot.jobportal.entity.User;
import com.springBoot.jobportal.repository.RecruiterProfileRepository;
import com.springBoot.jobportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RecruiterProfileService {

    private final RecruiterProfileRepository recruiterProfileRepository;
    private final UserRepository userRepository;

    @Autowired
    public RecruiterProfileService(RecruiterProfileRepository recruiterProfileRepository, UserRepository userRepository) {
        this.recruiterProfileRepository = recruiterProfileRepository;

        this.userRepository = userRepository;
    }

    public Optional<RecruiterProfile> getOne(Integer id){
        return recruiterProfileRepository.findById(id);
    }

    public RecruiterProfile addNew(RecruiterProfile recruiterProfile) {
        return recruiterProfileRepository.save(recruiterProfile);
    }

    public RecruiterProfile getCurrentRecruiterProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)){
            String username = authentication.getName();

            User user = userRepository.findByEmail(username).orElseThrow(()->
                    new UsernameNotFoundException("Could not found user."));

            Optional <RecruiterProfile> recruiterProfile = getOne(user.getUserId());
            return recruiterProfile.orElse(null);
        } else {
            return null;
        }
    }
}
