# AI News Summary Service - Backend API

Backend API for AI News Summary Service, built with Spring Boot and MySQL.

## 🛠 Tech Stack
- **Language:** Java 17
- **Framework:** Spring Boot 3.4.x
- **Database:** MySQL (Persistent Data) & **Redis** (Token Storage)
- **ORM:** Spring Data JPA
- **Security:** Spring Security & JWT (JSON Web Token)
- **Migration:** Flyway

## 🚀 Key Features
- **AI News Summarization:** Processes news content and generates summaries via AI integration.
- **Secure Authentication:** Implements JWT-based authentication with **Redis-backed Refresh Token management** for enhanced security and session control.
- **Credit System:** Manages user balances (coins) and tracks summarization request history.
- **Admin System:** Comprehensive APIs for monitoring users, managing credits, and auditing system activities.

## ⚙️ How to Run
1.  Locate `src/main/resources/application.properties.sample`.
2.  Copy and rename it to `application.properties`.
3.  Fill in your actual credentials (MySQL, Redis, API Keys).
4.  Ensure MySQL and Redis services are running.
5.  Run the application: `./mvnw spring-boot:run`
