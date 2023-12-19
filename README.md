# Task Manager API

[![build-and-test](https://github.com/biscof/task-manager-api/actions/workflows/build-and-test.yml/badge.svg)](https://github.com/biscof/task-manager-api/actions/workflows/build-and-test.yml)
[![Maintainability](https://api.codeclimate.com/v1/badges/9db80b9e352a908b4bf6/maintainability)](https://codeclimate.com/github/biscof/task-manager-api/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/9db80b9e352a908b4bf6/test_coverage)](https://codeclimate.com/github/biscof/task-manager-api/test_coverage)


## Overview

A RESTful API for managing tasks and comments. It enables users to create new tasks, edit existing ones, change their status and priority, assign other users to be in charge of tasks, and leave comments. The API makes extensive use of Spring Security features to provide authentication and authorization based on JWT tokens.


## Technologies and dependencies

- Java 20
- Spring Boot
- Spring Security
- Spring Data
- PostgreSQL
- Liquibase
- Hibernate
- JUnit


## Getting started

To run this app locally, you'll need:

- JDK 20
- Docker and Docker Compose

### Usage and development

1. Clone this repository and move to the project directory:

```bash
git clone https://github.com/biscof/task-manager-api.git
cd task-manager-api
```

2. Build and run the app locally with a containerized PostgreSQL database using Docker and Docker Cmpose:

```bash
make start-app-with-db
```

### Testing

To run tests use this command:

```bash
make test
```


## Base endpoints

- **Create a new user (sign up)**

```http
POST /api/users
Content-Type: application/json

{
    "firstName": "John",
    "lastName": "Smith", 
    "email": "john@smith.com"
    "password": "password"
}
```

- **Log in**

```http
POST /api/login
Content-Type: application/json

{
  "email": "john@smith.com",
  "password": "password"
}
```

Sample response body:

```
sample123.jwt456.token789
```

- **Create a new task**

```http
POST /api/tasks
Content-Type: application/json
Authorization: Bearer sample123.jwt456.token789

{
  "title": "Fix bugs",
  "description": "Description",
  "status": "NEW",
  "priority": "HIGH",
  "executorId": 0
}
```

- **Retrieve all tasks with a specific status, filtered by the author's and executor's IDs, and include pagination**

```http
GET /api/tasks/page=0&size=5&authorId=1&executorId=3&status=NEW
Authorization: Bearer sample123.jwt456.token789
```

- **Assign a task executor**

```http
PATCH /api/tasks/2/executor
Content-Type: application/json
Authorization: Bearer sample123.jwt456.token789

{
  "exectorId": 3
}
```

- **Create a new comment on the task**

```http
POST /api/comments
Content-Type: application/json
Authorization: Bearer sample123.jwt456.token789

{
  "title": "Hi",
  "content": "Ok",
  "taskId": 0
}
```

For other entrypoints, please refer to the API documentation .


## API Documentation

The documentation will be available on your host at `/docs/swagger-ui/index.html`.



