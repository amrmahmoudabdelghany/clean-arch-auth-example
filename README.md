# Clean Architecture Authentication Example

This repository showcases an authentication system built using Clean Architecture principles. It is designed for scalability, maintainability, and modularity.

## Features
- **Clean Architecture**: Clear separation of concerns across different layers.
- **Spring Boot**: A robust framework for developing authentication services.
- **JWT Authentication**: Secure authentication with JSON Web Tokens.
- **Spring Security**: Role-based access control for enhanced security.
- **REST API**: Well-structured authentication and user management endpoints.
- **Docker Support**: Easily deployable with Docker.
- **Swagger Documentation**: API documentation via Swagger UI.

## Project Structure
```
clean-arch-auth-example/
├── gray-auth-config        # Application configuration module
├── gray-auth-core          # Core business logic
│   ├── gray-auth-core-domain  # Domain models and interfaces
│   ├── gray-auth-core-usecase # Use case implementations
├── gray-auth-infra         # Infrastructure layer (database, external services)
└── README.md               # Project documentation
```

## Getting Started

### Prerequisites
- Java 17+
- Maven
- Docker (optional for containerized deployment)

### Installation
1. **Clone the repository:**
   ```sh
   git clone https://github.com/amrmahmoudabdelghany/clean-arch-auth-example.git
   cd clean-arch-auth-example
   ```
2. **Build the project:**
   ```sh
   mvn clean install
   ```
3. **Run the application:**
   ```sh
   mvn spring-boot:run
   ```

### Running with Docker
```sh
docker build -t clean-arch-auth .
docker run -p 8080:8080 clean-arch-auth
```

## API Documentation
Swagger UI is available at:
```
http://localhost:8080/swagger-ui/
```

## Contributing
1. Fork the repository.
2. Create a new branch (`feature/new-feature`).
3. Commit changes and push to your branch.
4. Open a Pull Request.

## License
This project is licensed under the MIT License.

## Contact
For any questions or suggestions, reach out:
- GitHub: [amrmahmoudabdelghany](https://github.com/amrmahmoudabdelghany)

