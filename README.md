# MultiGenesys E-Commerce Backend

Spring Boot backend for an e-commerce application built with JWT security, Spring Data JPA, validation, and Stripe payment support.

## Overview

This project provides the backend APIs for:

- user registration and login
- product management
- cart management
- order creation and retrieval
- payment processing

The application is designed to run with MySQL in normal mode and H2 for local development.

## Tech Stack

- Java 17
- Spring Boot 3.4.4
- Spring Web
- Spring Security
- Spring Data JPA
- Bean Validation
- JWT (`jjwt`)
- Stripe Java SDK
- MySQL
- H2

## Main Features

- JWT-based authentication
- Stateless Spring Security
- Public product listing endpoints
- Protected cart, order, and payment endpoints
- RESTful controllers for the core e-commerce workflow
- Environment-based datasource configuration

## Project Structure

- `src/main/java/com/multigenesys/ecommerce/controller` REST API controllers
- `src/main/java/com/multigenesys/ecommerce/service` service interfaces
- `src/main/java/com/multigenesys/ecommerce/service/impl` service implementations
- `src/main/java/com/multigenesys/ecommerce/repository` JPA repositories
- `src/main/java/com/multigenesys/ecommerce/entity` domain entities
- `src/main/java/com/multigenesys/ecommerce/dto` request and response models
- `src/main/java/com/multigenesys/ecommerce/security` JWT and security configuration
- `src/main/java/com/multigenesys/ecommerce/config` application setup and data initialization
- `src/main/java/com/multigenesys/ecommerce/exception` custom exceptions and handlers
- `src/main/java/com/multigenesys/ecommerce/util` helper classes and constants
- `src/main/resources` application configuration

## API Endpoints

### Authentication

- `POST /auth/register` register a user and return a JWT response
- `POST /auth/login` log in and return a JWT response

### Products

- `POST /products` create a product
- `GET /products` list all products
- `GET /products/{id}` get a product by id
- `PUT /products/{id}` update a product
- `DELETE /products/{id}` delete a product

### Cart

- `POST /cart/add` add an item to the cart
- `PUT /cart/update` update a cart item
- `GET /cart` fetch the current user cart
- `DELETE /cart/remove/{productId}` remove an item from the cart

### Orders

- `POST /orders/create` create an order
- `GET /orders/{orderId}` fetch a single order

### Payment

- `POST /payment` process a payment

## Security Rules

- `/auth/**` is public
- `GET /products/**` is public
- all other endpoints require authentication

## Configuration

The default configuration is in [`src/main/resources/application.yml`](src/main/resources/application.yml).

### Default application settings

- server port: `8080`
- datasource: MySQL
- JPA `ddl-auto`: `update`
- JWT and Stripe values are configured through application properties

### Development profile

Use the `dev` profile for local development with H2:

- H2 in-memory database
- H2 console enabled at `/h2-console`

### Production profile

The `prod` profile expects external environment variables:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

## Run Locally

### Prerequisites

- Java 17
- Maven
- MySQL if you want to run against the default datasource

### Start the application

```bash
mvn spring-boot:run
```

### Run with the dev profile

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Run tests

```bash
mvn test
```

## Database Notes

- By default, the app points to a MySQL database named `multigenesys`
- For local development, use the `dev` profile to switch to H2
- `application-prod.yml` is intended for deployment environments

## Important Notes

- Update secrets before production use
- The current codebase does not include Swagger/OpenAPI wiring yet
- Generated folders like `.sfdx/` and `target/` should not be committed

## License

No license has been defined yet.
