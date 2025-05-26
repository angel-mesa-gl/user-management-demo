# User Management Microservice

This document provides instructions for building and running the User Management Microservice.

## Prerequisites

* Java 11 JDK
* Git
* Gradle (wrapper is included)

## Setup

1.  **Clone the repository:**
    ```sh
    git clone https://github.com/angel-mesa-gl/user-management-demo.git
    cd user-management-demo
    ```
   
2.  **Database:**
    The project uses an H2 in-memory database, which is automatically configured and requires no external setup for local execution.

## Building the Project

To compile the code, run tests, and create the executable JAR, you can:

* **macOS / Linux:**
    ```sh
    ./gradlew clean build
    ```
* **Windows:**
    ```sh
    gradlew clean build
    ```

## Running the Application

You can run the application with the following command or by executing the JAR file directly.

**1. Using `bootRun`:**

* **macOS / Linux:**
    ```sh
    ./gradlew bootRun
    ```
* **Windows:**
    ```sh
    gradlew bootRun
    ```

**2. Running the JAR:**
The build process generates an executable JAR in the `build/libs/` directory.
```sh
java -jar build/libs/user-management-demo-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8080`.

## API Endpoints & Execution

Once the application is running, you can test the following endpoints using a tool like Postman or cURL:

### 1. User Sign-Up

* Endpoint: POST /sign-up
* Headers: Content-Type: application/json
* Example Request Body:
    ```json
    {
    "name": "Julio Gonzalez",
    "email": "julio@julio.com",
    "password": "a2asfGfdfdf4",
    "phones": [
      {
      "number": 87650009,
      "citycode": 7,
      "countrycode": "25"
      },
      {
      "number": 12345678,
      "citycode": 1,
      "countrycode": "01"
      }
    ]
    }
    ```
* **Example Success Response (HTTP 201 Created):**
    ```json
    {
    "id": "14b29376-d47b-443c-8a8f-c95d167ad726",
    "created": "May 25, 2025 07:43:46 PM",
    "lastLogin": "May 25, 2025 07:43:46 PM",
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxNGIyOTM3Ni1kNDdiLTQ0M2MtOGE4Zi1jOTVkMTY3YWQ3MjYiLCJpYXQiOjE3NDgyMjc0MjYsImV4cCI6MTc0ODIzMTAyNn0.jtBYoBc5HIB5Ex3q88RMjaLldJoBgHhsmpxrGS6pEFk",
    "isActive": true
    }
    ```

### 2. User Login / Refresh Token

* Endpoint: POST /login
* Headers: Content-Type: application/json
* Example Request Body:
    ```json
    {
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyNjRiODkwZi0wMjU3LTQxMWMtYWIzOS01OWJkMmEzNjI2NjEiLCJpYXQiOjE3NDgyMjU2MTcsImV4cCI6MTc0ODIyOTIxN30.tAEarIHmwMwlHn5PI2PjH22_VEbjenE2zI0CbMwUZa8"
    }   
    ```
* **Example Success Response (HTTP 200 OK):**
    ```json
    {
    "id": "14b29376-d47b-443c-8a8f-c95d167ad726",
    "created": "May 25, 2025 07:43:46 PM",
    "lastLogin": "May 25, 2025 07:43:46 PM",
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxNGIyOTM3Ni1kNDdiLTQ0M2MtOGE4Zi1jOTVkMTY3YWQ3MjYiLCJpYXQiOjE3NDgyMjc2MjMsImV4cCI6MTc0ODIzMTIyM30.usEMLD7wZezt0jkfu9j2e-ireQgsMsive7B6k6DpeiM",
    "isActive": true,
    "name": "Julio Gonzalez",
    "email": "julio@julio.com",
    "password": "$2a$10$LsTUhxaqDOBAFAZ37bSO9.tMR7a45Tj4Hb1GBFDHoDF1taQDhimm.",
    "phones": [
      {
      "number": 87650009,
      "citycode": 7,
      "countrycode": "25"
      },
      {
      "number": 12345678,
      "citycode": 1,
      "countrycode": "01"
      }
    ]
    }
    ```

### Error Responses
All errors return a JSON body in the following format:
```
{
    "error": [
        {
            "timestamp": Timestamp,
            "codigo": int,
            "detail": String
        }
    ]
}
```

## Accessing H2 Console

To view the H2 in-memory database during development:
* URL: http://localhost:8080/h2-console
* JDBC URL: jdbc:h2:mem:testdb
* User Name: sa
* Password: password