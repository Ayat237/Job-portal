package com.springBoot.jobportal.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "job_location")
public class JobLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    private String city;

    private String country;

    private String state;

    public JobLocation() {
    }

    public JobLocation(Integer id, String state, String country, String city) {
        this.id = id;
        this.state = state;
        this.country = country;
        this.city = city;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "JobLocation{" +
                "id=" + id +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}
