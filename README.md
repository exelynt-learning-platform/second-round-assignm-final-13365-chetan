# E-commerce Backend Scaffold

This repository is set up as a Spring Boot backend starter for an e-commerce system.

## What is included

- Spring Boot 3.4.x project skeleton
- JWT-friendly security setup dependencies
- Spring Data JPA
- Validation
- H2 for local development
- MySQL driver for later database switching
- Stripe SDK for payment integration
- Test scaffold

## Folder structure

- `controller/` REST endpoints
- `service/` business logic interfaces
- `service/impl/` service implementations
- `repository/` JPA repositories
- `entity/` database entities
- `dto/` request and response models
- `security/` JWT and Spring Security configuration
- `config/` app configuration
- `exception/` custom errors and handlers
- `util/` helpers and constants

## Next steps

1. Add entities for user, product, cart, and order.
2. Add JWT authentication and role-based security.
3. Add CRUD APIs for products.
4. Add cart and order APIs.
5. Add payment service integration.
