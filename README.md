# Spring Boot Job Portal

A full-stack web application built with **Spring Boot** that connects **recruiters** and **job seekers** through a simple, secure, and efficient job portal system.  
Recruiters can post and manage job listings, view applicants, download résumés, and update their profiles — while job seekers can search and apply for jobs,save jobs, upload résumés, and manage their own profiles.

---

## Features

### 👨‍💼 Recruiter Module
- Post new job listings  
- View and manage existing job posts  
- See the list of candidates who applied for each job  
- Download applicants’ résumés  
- Edit profile and upload profile photo  

### 👩‍💻 Candidate Module
- Search for jobs by title or Location
- - Search for jobs by Filters
- Apply for jobs directly through the portal
- Save jobs 
- View a list of jobs you’ve applied for  
- Upload and manage résumé / CV  
- Edit profile and upload profile photo

### 🌍 Global Feature
- Users can **search and explore job listings without logging in**, with the ability to **filter results** by job title, category, or location for a smoother browsing experience.  

### 🔐 Common Features
- Register for a new account  
- Secure login/logout functionality  
- Role-based access (Recruiter / Candidate)  
- Custom authentication success handler  

---


## 🏗️ Development Process
1. Set up Spring Boot project with Maven  
2. Added HTML, JS, and CSS templates using Thymeleaf  
3. Created database entities for Users, Jobs, and Applications  
4. Implemented user registration and login modules  
5. Built recruiter and candidate dashboards  
6. Integrated résumé upload/download functionality  
7. Added Spring Security for authentication and authorization  
8. Created custom authentication success handler  
9. Integrated profile management for both roles  
10. Tested application flows and refined UI  

---

## ⚙️ Tech Stack

| Layer | Technologies |
|-------|---------------|
| **Language** | Java 17 |
| **Framework** | Spring Boot 3.2 |
| **Security** | Spring Security |
| **Database** | MySQL |
| **ORM** | JPA / Hibernate |
| **Frontend** | Thymeleaf, HTML, CSS, JavaScript |
| **Build Tool** | Maven |
| **IDE** | IntelliJ IDEA |
| **Version Control** | Git & GitHub |

---

## 🧰 Installation & Setup

### Prerequisites
- JDK 17 or higher  
- Maven 3+  
- MySQL  

### Steps
1. **Clone the repository**
2. **Configure the database**
Update your application.properties file:

spring.datasource.url=jdbc:mysql://localhost:3306/job_portal_db
spring.datasource.username=your_username
spring.datasource.password=your_password

2. **Build and run the application**
   ```bash
   mvn clean install
   mvn spring-boot:run
  or build direct from IntelliJ
4. **Access the app**
Open your browser and go to:
[http://localhost:8080](http://localhost:8080)



