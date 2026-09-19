# 🏨 Hotel Booking System (REST API)

A robust backend REST API for managing hotel rooms, guest records, and room reservations. Originally built as a core Java JDBC application, this project has been fully migrated to an enterprise-grade **Spring Boot** layered architecture using **Spring Data JPA** and **MySQL**.

---

## 🚀 Features

- **Room Management:** Query all rooms or filter specifically by real-time availability.
- **Reservation Engine:** Reserve rooms with automated date range checks, room availability verification, and automated nightly rate billing calculations.
- **Guest Handling:** Automatically creates new guest profiles or reuses existing profiles based on email addresses.
- **Validation:** Enforces check-in/check-out constraints and payload formatting via Jakarta Bean Validation.
- **Database Persistence:** Managed by Spring Data JPA (Hibernate) with HikariCP connection pooling.

---

## 🛠️ Tech Stack

- **Language:** Java 17 / 21
- **Framework:** Spring Boot 3.2.5
  - `spring-boot-starter-web` (REST APIs, Embedded Tomcat)
  - `spring-boot-starter-data-jpa` (Hibernate ORM & Repositories)
  - `spring-boot-starter-validation` (Jakarta Validation)
- **Database:** MySQL
- **Build Tool:** Maven

---

## 🏗️ Architecture

The codebase follows a standard 4-tier layered architecture:

```text
com.hotel
├── controller   # REST Endpoints (HTTP request handling)
├── service      # Business logic, pricing, validation, transactions
├── repository   # Spring Data JPA repositories (SQL queries)
├── model        # JPA database entities (Room, Guest, Booking)
└── dto          # Request and response data transfer objects