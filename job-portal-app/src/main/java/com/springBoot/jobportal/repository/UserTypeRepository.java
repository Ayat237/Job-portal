package com.springBoot.jobportal.repository;

import com.springBoot.jobportal.entity.UserType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTypeRepository extends JpaRepository <UserType, Integer> {
}
