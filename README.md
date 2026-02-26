RevConnect — Full Stack Social Media Platform

A professional full-stack social media application built with Spring Boot (backend) and Angular (frontend).

Tech Stack
Layer	Technology
Backend	Java 17, Spring Boot 3.2, Spring Security, Spring Data JPA
Auth	JWT (JJWT 0.11.5), BCrypt
Database	MySQL 8, Hibernate ORM
Logging	Log4J2
Testing	JUnit, Mockito
Build	Maven
Frontend	Angular 17, TypeScript, Bootstrap 5
Runtime	Node.js 18+
Project Structure
revconnect/
├── backend/
│   └── src/main/java/com/revconnect/
│       ├── auth/
│       ├── user/
│       ├── post/
│       ├── comment/
│       ├── like/
│       ├── network/
│       ├── notification/
│       ├── feed/
│       ├── security/
│       └── common/
│
├── frontend/
│   └── src/app/
│       ├── core/
│       ├── shared/
│       └── features/
│
└── database/
    └── schema.sql
Setup Instructions
1. Create Database
CREATE DATABASE revconnect_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
2. Configure Backend

Edit:

backend/src/main/resources/application.properties

Add your MySQL credentials:

spring.datasource.url=jdbc:mysql://localhost:3306/revconnect_db
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
3. Run Backend
cd backend
mvn clean install
mvn spring-boot:run

Backend runs at:

http://localhost:8080/api
4. Run Frontend
cd frontend
npm install
npm start

Frontend runs at:

http://localhost:4200
Git Workflow
git checkout develop
git pull origin develop
git checkout feature/your-feature
git merge develop
git add .
git commit -m "your message"
git push origin feature/your-feature
Security Features

BCrypt password hashing

JWT authentication

Spring Security authorization

Input validation using @Valid

CORS configuration

Built by RevConnect Team — 2024