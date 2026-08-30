# 🚀 RecoverX

> **A full-stack lost-and-found management platform built with Java, Spring Boot, PostgreSQL, and Docker.**

🌐 **Live Demo:** https://recoverx-09cg.onrender.com/

💻 **GitHub Repository:** https://github.com/Jayasurya2006/RecoverX

---

## 📌 About

**RecoverX** is a full-stack web application designed to simplify the process of reporting, managing, and recovering lost and found items through a centralized platform.

The application combines a web interface with a **Spring Boot REST API**, **PostgreSQL database**, and **JPA/Hibernate** for persistent data management. The application is containerized using **Docker** and deployed to the cloud using **Render**.

---

## ✨ Key Features

* 🔐 User registration and authentication
* 📦 Lost item reporting
* 🔎 Found item reporting
* 📋 Item information management
* 🗄️ PostgreSQL database integration
* 🌐 RESTful backend APIs
* 📱 Web-based user interface
* 🤖 AI-based item detection module
* 🐳 Dockerized application
* ☁️ Cloud deployment using Render

---

## 🤖 AI Detection

RecoverX includes an **AI-based detection module** designed to assist with item identification and classification.

The module is integrated into the project as part of the application's intelligent item-processing functionality.

> **Note:** The AI detection functionality is currently disabled in the production deployment because it depends on a paid third-party service.

---

## 🛠️ Technology Stack

| Layer                | Technologies               |
| -------------------- | -------------------------- |
| **Frontend**         | HTML, CSS, JavaScript      |
| **Backend**          | Java, Spring Boot          |
| **API**              | REST APIs                  |
| **ORM**              | Spring Data JPA, Hibernate |
| **Database**         | PostgreSQL                 |
| **Build Tool**       | Maven                      |
| **AI Module**        | AI-based item detection    |
| **Containerization** | Docker                     |
| **Deployment**       | Render                     |

---

## 🏗️ System Architecture

```text
                    ┌─────────────────────┐
                    │    User / Browser   │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │    Frontend Web UI  │
                    │    HTML/CSS/JS      │
                    └──────────┬──────────┘
                               │
                          HTTP / REST
                               │
                               ▼
                    ┌─────────────────────┐
                    │   Spring Boot API   │
                    │        Java         │
                    └──────┬─────────┬────┘
                           │         │
                    JPA / Hibernate  │
                           │         │
                           ▼         ▼
                    ┌───────────┐  ┌──────────────┐
                    │ PostgreSQL│  │ AI Detection │
                    │ Database  │  │    Module    │
                    └───────────┘  └──────────────┘
```

---

## 📂 Project Structure

```text
RecoverX/
│
├── recoverx-backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   └── resources/
│   │   └── test/
│   │
│   ├── Dockerfile
│   └── pom.xml
│
├── README.md
└── ...
```

---

## 🔌 REST API

The Spring Boot backend exposes RESTful APIs for handling application operations and database interactions.

The API layer follows a structured backend architecture using:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
PostgreSQL
```

This separation helps maintain a clean and maintainable backend structure.

---

## 🧠 Backend Architecture

RecoverX follows a layered Spring Boot architecture:

### Controller Layer

Handles incoming HTTP requests and exposes REST endpoints.

### Service Layer

Contains application/business logic and coordinates operations between controllers and repositories.

### Repository Layer

Uses **Spring Data JPA** to communicate with the PostgreSQL database.

### Entity Layer

Defines the application's persistent data models using JPA entities.

---

## ⚙️ Running Locally

### 1. Clone the repository

```bash
git clone https://github.com/Jayasurya2006/RecoverX.git
cd RecoverX
```

### 2. Configure PostgreSQL

Create a PostgreSQL database named:

```text
recoverx
```

Configure the required environment variables:

```properties
DB_URL=jdbc:postgresql://localhost:5432/recoverx
DB_USERNAME=your_username
DB_PASSWORD=your_password
```

> ⚠️ **Never commit real database credentials, API keys, or other secrets to GitHub.**

### 3. Run the backend

```bash
cd recoverx-backend
mvn spring-boot:run
```

The application will start on:

```text
http://localhost:8080
```

---

## 🐳 Docker

RecoverX can be built and run using Docker.

### Build the image

```bash
docker build -t recoverx .
```

### Run the container

```bash
docker run -p 8080:8080 recoverx
```

The application will then be available at:

```text
http://localhost:8080
```

---

## ☁️ Deployment

RecoverX is deployed using:

* 🐳 **Docker** — application containerization
* ☁️ **Render** — cloud deployment
* 🗄️ **PostgreSQL** — production database
* 🔐 **Environment Variables** — production configuration and secrets

### Deployment Flow

```text
GitHub
   │
   ▼
Docker Build
   │
   ▼
Render
   │
   ▼
Spring Boot Application
   │
   ▼
PostgreSQL
```

### 🌐 Live Application

**RecoverX:**
https://recoverx-09cg.onrender.com/

---

## 🔒 Security & Configuration

Sensitive production configuration is managed through environment variables.

* Database credentials are not committed to the repository.
* Production database configuration is stored separately from source code.
* API/service credentials are kept outside the GitHub repository.
* Environment-specific configuration is used for deployment.

---


## 🎯 What I Learned

Through building and deploying RecoverX, I gained practical experience in:

* Developing backend applications using **Java and Spring Boot**
* Building **RESTful APIs**
* Working with **Spring Data JPA and Hibernate**
* Integrating **PostgreSQL** with a Spring Boot application
* Designing layered backend architecture
* Managing application configuration using environment variables
* Containerizing applications using **Docker**
* Deploying applications to the cloud using **Render**
* Debugging production database connection issues
* Integrating an AI-based detection module into a web application

---

## 🔮 Future Enhancements

Potential improvements include:

* 🔔 Real-time notifications
* 📍 Location-based lost-and-found matching
* 📧 Email notifications
* 🔎 Advanced search and filtering
* 📊 Admin dashboard
* 📱 Progressive Web App support
* 🤖 Re-enable AI detection using a cost-effective or self-hosted solution

---

## 👨‍💻 Developer

### Jayasurya P

**BE Computer Science & Engineering (IoT)**

🔗 **GitHub:**
https://github.com/Jayasurya2006

🔗 **Portfolio:**
https://jayasurya2006.github.io/Jayasurya-Portfolio/

---

## ⭐ Project Status

🟢 **Live and deployed**

RecoverX is currently deployed and accessible through the live demo.

> **Note:** The core web application and backend are deployed and operational. The AI detection module is implemented in the project but is currently disabled in production because of its paid third-party service dependency.
