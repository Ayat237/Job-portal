package com.springBoot.jobportal.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "job_company")
public class JobCompany {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String logo;

    private String name;

    public JobCompany() {
    }

    public JobCompany(String name, String logo, Integer id) {
        this.name = name;
        this.logo = logo;
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "JobCompany{" +
                "id=" + id +
                ", logo='" + logo + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
