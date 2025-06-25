# Cấu trúc Project

[dbdocs]
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── vissoft
│   │   │           └── vn
│   │   │               └── dbdocs
│   │   │                   ├── application
│   │   │                   │   ├── dto/
│   │   │                   │   ├── port/
│   │   │                   │   ├── service/
│   │   │                   │   └── usecase/
│   │   │                   ├── config
│   │   │                   ├── domain
│   │   │                   │   ├── entity/
│   │   │                   │   ├── exception/
│   │   │                   │   ├── model/
│   │   │                   │   ├── repository/
│   │   │                   │   └── service/
│   │   │                   ├── infrastructure
│   │   │                   │   ├── adapter/
│   │   │                   │   ├── config/
│   │   │                   │   ├── constant/
│   │   │                   │   ├── exception/
│   │   │                   │   ├── filter/
│   │   │                   │   ├── mapper/
│   │   │                   │   ├── persistence/
│   │   │                   │   │   ├── entity/
│   │   │                   │   │   └── repository/
│   │   │                   │   ├── security/
│   │   │                   │   └── util/
│   │   │                   ├── interfaces
│   │   │                   │   ├── exception/
│   │   │                   │   └── rest/
│   │   │                   │       ├── dto/
│   │   │                   │       └── impl/
│   │   │                   └── DbdocsApplication.java
│   │   └── resources
│   │       ├── static
│   │       ├── templates
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-prod.yml
│   │       └── ...
│   └── test
│       └── ...
├── .gitignore
├── pom.xml
├── Dockerfile
├── docker-compose.yml
├── README.md
├── struct.md
└── ...

## Giải thích các thành phần chính

- **src/main/java/com/vissoft/vn/dbdocs/**: Chứa mã nguồn chính của ứng dụng, chia thành các layer:
  - **application/**: Chứa các lớp service, DTO, port, usecase phục vụ cho logic ứng dụng.
  - **domain/**: Chứa các entity, repository, model, service, exception liên quan đến domain.
  - **infrastructure/**: Chứa các thành phần liên quan đến hạ tầng như util, security, mapper, persistence, config...
  - **interfaces/**: Chứa các lớp controller (rest), exception handler phục vụ giao tiếp với bên ngoài.
  - **config/**: Các file cấu hình Spring, OpenAPI, Swagger, Javers...
  - **DbdocsApplication.java**: Lớp main khởi động ứng dụng Spring Boot.

- **src/main/resources/**: Chứa các file cấu hình, template, static resource, message...

- **src/test/**: Chứa mã nguồn test.

- **pom.xml**: File cấu hình Maven.
- **Dockerfile, docker-compose.yml**: Cấu hình Docker.
- **README.md, struct.md, HELP.md**: Tài liệu dự án.

Các thư mục khác như `.git`, `.idea`, `.vscode`, `.mvn`, `target` là các thư mục hệ thống, cấu hình IDE, build.

# Hexagonal Architecture (Ports & Adapters) Structure

src/main/java/com/vissoft/vn/dbdocs/
├── DbdocsApplication.java
├── package-info.java               # Package documentation
├── domain/                         # Domain Layer (Core Business Logic)
│   ├── package-info.java          # Domain layer documentation
│   ├── model/                     # Domain Models/Entities
│   │   └── package-info.java      # Domain models documentation
│   ├── repository/                # Domain Repository Interfaces
│   │   └── package-info.java      # Domain repositories documentation
│   └── service/                   # Domain Services
│       └── package-info.java      # Domain services documentation
├── application/                   # Application Layer (Use Cases & Ports)
│   ├── package-info.java          # Application layer documentation
│   ├── dto/                      # Application DTOs
│   │   └── package-info.java      # Application DTOs documentation
│   ├── usecase/                  # Use Cases (Business Workflows)
│   │   └── package-info.java      # Use cases documentation
│   └── port/                     # Application Ports (Interfaces)
│       └── package-info.java      # Ports documentation
├── infrastructure/               # Infrastructure Layer (Adapters)
│   ├── package-info.java          # Infrastructure layer documentation
│   ├── persistence/              # Persistence Adapters
│   │   ├── package-info.java      # Persistence documentation
│   │   ├── entity/              # JPA Entities
│   │   │   └── package-info.java  # JPA entities documentation
│   │   └── repository/          # Repository Adapters
│   │       └── package-info.java  # Repository adapters documentation
│   └── config/                  # Configuration Classes
│       └── package-info.java      # Configuration documentation
└── interfaces/                 # Interface Layer (External Adapters)
    ├── package-info.java          # Interface layer documentation
    ├── rest/                     # REST Adapters
    │   ├── package-info.java      # REST documentation
    │   └── dto/                  # REST DTOs
    │       └── package-info.java  # REST DTOs documentation
    └── exception/               # Exception Handlers
        └── package-info.java      # Exception handlers documentation

## Mô tả từng layer:

### 1. Domain Layer (domain/)
- **model/**: Chứa các domain models/entities - pure business objects
- **repository/**: Chứa các domain repository interfaces
- **service/**: Chứa các domain services với business rules

### 2. Application Layer (application/)
- **dto/**: Chứa các Application DTOs cho data transfer
- **usecase/**: Chứa các use cases - business workflows và orchestration
- **port/**: Chứa các port interfaces (contracts với external systems)

### 3. Infrastructure Layer (infrastructure/)
- **persistence/entity/**: Chứa JPA entities và database models
- **persistence/repository/**: Chứa repository adapters implement ports
- **config/**: Chứa các configuration classes (Spring Config, etc.)

### 4. Interface Layer (interfaces/)
- **rest/**: Chứa REST controllers và HTTP adapters
- **rest/dto/**: Chứa REST-specific DTOs (requests/responses)
- **exception/**: Chứa global exception handlers

## Nguyên tắc Dependency (Hexagonal Architecture):

```
Interfaces → Application → Domain ← Infrastructure
     ↓           ↓          ↑           ↑
   (HTTP)    (Use Cases) (Models)   (Database)
```

### Dependency Rules:
- **Domain**: Không phụ thuộc vào layer nào - pure business logic
- **Application**: Chỉ phụ thuộc vào Domain - chứa use cases và ports
- **Infrastructure**: Implement các ports từ Application layer
- **Interfaces**: Phụ thuộc vào Application layer để call use cases

### Key Principles:
- **Domain**: Không import Spring hay JPA
- **Application**: Chứa logic use-case, chỉ phụ thuộc vào domain
- **Infrastructure**: Chỉ chứa các adapter cụ thể (Spring Data JPA)
- **Interfaces**: Controllers, mapping từ HTTP → DTO → use-case

## Lợi ích của Hexagonal Architecture:

1. **Separation of Concerns**: Mỗi layer có trách nhiệm rõ ràng và độc lập
2. **Testability**: Dễ dàng test business logic mà không cần framework
3. **Flexibility**: Dễ thay đổi database, web framework mà không ảnh hưởng business logic
4. **Independence**: Business logic hoàn toàn độc lập với external concerns
5. **Maintainability**: Code structure rõ ràng, dễ bảo trì và mở rộng
6. **Domain-Driven**: Focus vào business domain, không bị ràng buộc bởi technical constraints

## Port & Adapter Pattern:

### Ports (Interfaces):
- **Primary Ports**: Use case interfaces (called by external adapters)
- **Secondary Ports**: Repository, external service interfaces (implemented by infrastructure)

### Adapters:
- **Primary Adapters**: REST controllers, CLI, messaging consumers
- **Secondary Adapters**: Database repositories, external API clients, file systems

### Example Flow:
```
HTTP Request → REST Controller → Use Case → Domain Service → Repository Port → JPA Repository → Database
```

## Package Documentation:

Mỗi package đều có file `package-info.java` để:
- Mô tả mục đích và chức năng của package
- Cung cấp thông tin về kiến trúc và nguyên tắc thiết kế
- Hướng dẫn sử dụng và best practices
- Ghi chú về dependencies và relationships

### Cấu trúc package-info.java:
```java
/**
 * Package description and purpose
 * 
 * Detailed explanation of what this package contains
 * and how it fits into the overall architecture.
 * 
 * @author VIS Software
 */
package com.vissoft.vn.dbdocs.layername;
```

Điều này giúp:
- Developers mới hiểu rõ cấu trúc project
- Maintain documentation cùng với code
- IDE support với package-level documentation
- Improve code discoverability

