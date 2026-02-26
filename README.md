# RevConnect — Full Stack Social Media Platform

A professional full-stack social media application built with **Spring Boot** (backend) and **Angular** (frontend).

---

## Tech Stack

| Layer       | Technology                                          |
|-------------|-----------------------------------------------------|
| Backend     | Java 17, Spring Boot 3.2, Spring Security, Spring Data JPA |
| Auth        | JWT (JJWT 0.11.5), BCrypt                           |
| Database    | MySQL 8, Hibernate ORM (auto DDL)                   |
| Logging     | Log4J2                                              |
| Testing     | JUnit 4, Mockito                                    |
| Build       | Maven                                               |
| Frontend    | Angular 17, TypeScript, Bootstrap 5                 |
| Runtime     | Node.js 18+                                         |

---

## Project Structure

```
revconnect/
├── backend/           ← Spring Boot application
│   └── src/main/java/com/revconnect/
│       ├── auth/          ← Register / Login / JWT
│       ├── user/          ← Profile management
│       ├── post/          ← Post CRUD + Analytics
│       ├── comment/       ← Comments
│       ├── like/          ← Likes / Unlikes
│       ├── network/       ← Connections + Follows
│       ├── notification/  ← Notification engine
│       ├── feed/          ← Personalized feed
│       ├── security/      ← JWT filter + Security config
│       └── common/        ← DTOs, exceptions, utils
│
├── frontend/          ← Angular application
│   └── src/app/
│       ├── core/          ← Services, Guards, Interceptors
│       ├── shared/        ← Models, Navbar
│       └── features/      ← Auth, Feed, Profile, Network, Notifications
│
└── database/
    └── schema.sql     ← Reference SQL (Hibernate auto-creates tables!)
```

---

## Prerequisites

- **Java 17** or higher
- **Maven 3.8+**
- **MySQL 8.0+**
- **Node.js 18+** and **npm**
- **Angular CLI**: `npm install -g @angular/cli`

---

## Setup Instructions

### Step 1: MySQL Database

```sql
CREATE DATABASE revconnect_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

> Tables are **automatically created by Hibernate** when you start the backend.
> No need to run `schema.sql` manually — it is for reference only.

---

### Step 2: Backend Configuration

Edit `backend/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/revconnect_db?...
spring.datasource.username=YOUR_MYSQL_USERNAME
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

---

### Step 3: Run the Backend

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

Backend starts at: **http://localhost:8080/api**

---

### Step 4: Run the Frontend

```bash
cd frontend
npm install
npm start
```

Frontend starts at: **http://localhost:4200**

The proxy config automatically routes `/api` requests to the backend.

---

## All API Endpoints

### Auth
| Method | Endpoint              | Description       |
|--------|----------------------|-------------------|
| POST   | `/api/auth/register` | Register          |
| POST   | `/api/auth/login`    | Login (JWT)       |

### Users
| Method | Endpoint              | Description       |
|--------|----------------------|-------------------|
| GET    | `/api/users/me`      | Current user      |
| GET    | `/api/users/{id}`    | Get profile       |
| PUT    | `/api/users/{id}`    | Update profile    |
| GET    | `/api/users/search?q=` | Search users   |

### Posts
| Method | Endpoint                    | Description           |
|--------|-----------------------------|-----------------------|
| POST   | `/api/posts`               | Create post           |
| GET    | `/api/posts/{id}`          | Get post              |
| PUT    | `/api/posts/{id}`          | Update post           |
| DELETE | `/api/posts/{id}`          | Delete post           |
| GET    | `/api/posts/user/{userId}` | User's posts          |
| GET    | `/api/posts/trending`      | Trending posts        |
| GET    | `/api/posts/search?hashtag=` | Search by hashtag   |
| GET    | `/api/posts/{id}/analytics` | Post analytics       |

### Likes & Comments
| Method | Endpoint                            | Description         |
|--------|-------------------------------------|---------------------|
| POST   | `/api/posts/{id}/like`             | Like a post         |
| DELETE | `/api/posts/{id}/like`             | Unlike a post       |
| POST   | `/api/posts/{id}/comments`         | Add comment         |
| GET    | `/api/posts/{id}/comments`         | Get comments        |
| DELETE | `/api/comments/{id}`               | Delete comment      |

### Network
| Method | Endpoint                                     | Description           |
|--------|----------------------------------------------|-----------------------|
| POST   | `/api/network/connect/{userId}`             | Send connection request |
| PUT    | `/api/network/connections/{id}/accept`      | Accept request        |
| PUT    | `/api/network/connections/{id}/reject`      | Reject request        |
| DELETE | `/api/network/connect/{userId}`             | Remove connection     |
| GET    | `/api/network/connections`                  | My connections        |
| GET    | `/api/network/requests/received`            | Pending requests      |
| GET    | `/api/network/requests/sent`                | Sent requests         |
| POST   | `/api/network/follow/{userId}`              | Follow user           |
| DELETE | `/api/network/follow/{userId}`              | Unfollow              |
| GET    | `/api/network/followers/{userId}`           | Get followers         |
| GET    | `/api/network/following/{userId}`           | Get following         |

### Feed & Notifications
| Method | Endpoint                         | Description            |
|--------|----------------------------------|------------------------|
| GET    | `/api/feed`                     | Personalized feed      |
| GET    | `/api/notifications`            | All notifications      |
| GET    | `/api/notifications/unread-count` | Unread count         |
| PUT    | `/api/notifications/{id}/read`  | Mark one read          |
| PUT    | `/api/notifications/read-all`   | Mark all read          |

---

## Team Division (6 Members)

| Member | Owns |
|--------|------|
| Team Lead | Repo setup, Security config, JWT, application.properties |
| Backend Dev 1 | Auth module + User module |
| Backend Dev 2 | Post + Comment + Like modules |
| Backend Dev 3 | Network + Notification + Feed modules |
| Frontend Dev 1 | Auth pages, Profile, Feed |
| Frontend Dev 2 | Network, Notifications, Post detail, Analytics |

---

## Git Workflow

```bash
# Branch naming
feature/auth-backend
feature/post-backend
feature/network-backend
feature/auth-frontend
feature/feed-frontend

# Daily workflow
git checkout develop
git pull origin develop
git checkout feature/your-feature
git merge develop
# code → commit → push → Pull Request → review → merge to develop
```

---

## Running Tests

```bash
cd backend
mvn test
```

---

## Security Features

- **BCrypt** password hashing (strength 12)
- **JWT** authentication (HS512 algorithm)
- **Spring Security** authorization on all endpoints
- **Input validation** via `@Valid` + Bean Validation
- **SQL Injection** prevented via JPA/Hibernate parameterized queries
- **XSS** prevented via Spring's response encoding
- **CORS** configured for Angular origin only

---

## ERD Summary

```
users ──< posts ──< comments
  |           |
  |           └──< likes
  |
  ├──< connections (self-join)
  ├──< follows     (self-join)
  └──< notifications
```

---

*Built by the RevConnect Team — 2024*
