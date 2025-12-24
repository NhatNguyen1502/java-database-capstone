# Smart Clinic – Architecture Overview

This document provides a concise overview of the **Smart Clinic** application architecture, focusing on request flow, layer responsibilities, and technology interactions. The system follows **Spring Boot best practices** with a clear separation of concerns.

---

## 1. User Interface Layer

The application supports two interaction models:

* **Server-rendered web UI (Thymeleaf)**: Admin and Doctor dashboards rendered as HTML on the server.
* **REST API clients**: Mobile apps or frontend modules (Appointments, PatientDashboard, PatientRecord) communicating via HTTP/JSON.

This enables both traditional web usage and scalable API-based integrations.

---

## 2. Numbered Flow of Data and Control

This section describes the step-by-step flow of data and control through the Smart Clinic system for a typical request.

1. A user interacts with the system via a web browser (Thymeleaf UI) or an API client.
2. The client sends an HTTP request to the backend application.
3. Spring Boot routes the request to the appropriate controller based on the URL and HTTP method.
4. The controller validates input data and delegates processing to the service layer.
5. The service layer applies business rules and coordinates required operations.
6. If data access is required, the service calls the appropriate repository.
7. The repository interacts with MySQL or MongoDB to retrieve or persist data.
8. Retrieved data is mapped to application models (entities or documents).
9. The service layer processes the results and returns them to the controller.
10. The controller prepares the response:

    * HTML view for MVC requests
    * JSON payload for REST requests
11. The response is sent back to the client, completing the request–response cycle.

---

## 3. Controller Layer

Controllers act as the entry point for HTTP requests:

* **Thymeleaf Controllers** return HTML views populated with model data.
* **REST Controllers** process API requests and return JSON responses.

Responsibilities include request routing, validation, and coordinating the request–response flow.

---

## 3. Service Layer

The **Service Layer** contains all business logic and workflows.

Key responsibilities:

* Apply business rules and validations
* Coordinate operations across multiple entities
* Decouple controllers from data access logic

This layer improves maintainability, testability, and scalability.

---

## 4. Repository Layer

Repositories abstract database access using Spring Data:

* **MySQL (Spring Data JPA)** for relational data such as patients, doctors, appointments, and admins.
* **MongoDB (Spring Data MongoDB)** for document-based data such as prescriptions.

---

## 5. Databases

* **MySQL** stores normalized, relational core data with constraints.
* **MongoDB** stores flexible, nested documents that support schema evolution.

This polyglot persistence approach leverages the strengths of both database types.

---

## 6. Model Binding

Database records are mapped to Java models:

* **JPA Entities (`@Entity`)** for MySQL tables
* **MongoDB Documents (`@Document`)** for MongoDB collections

These models provide a consistent object-oriented representation across layers.

---

## 7. Response Handling

* **MVC flow**: Models are passed to Thymeleaf templates and rendered as HTML.
* **REST flow**: Models or DTOs are serialized into JSON responses.

This completes the request–response lifecycle.

---

## Summary

The Smart Clinic architecture features:

* Clear layer separation
* Centralized business logic
* Dual UI support (MVC + REST)
* Combined use of MySQL and MongoDB

This design ensures flexibility, scalability, and long-term maintainability.
