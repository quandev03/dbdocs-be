# Clean Architecture Structure

src/main/java/com/vissoft/vn/dbdocs/
├── DbdocsApplication.java
├── package-info.java               # Package documentation
├── domain/                         # Domain Layer (Core Business Logic)
│   ├── package-info.java          # Domain layer documentation
│   ├── entities/                  # Business Entities
│   │   └── package-info.java      # Entities documentation
│   ├── repositories/              # Repository Interfaces (Contracts)
│   │   └── package-info.java      # Repositories documentation
│   └── services/                  # Domain Services
│       └── package-info.java      # Domain services documentation
├── application/                   # Application Layer (Use Cases)
│   ├── package-info.java          # Application layer documentation
│   ├── usecases/                 # Use Cases / Business Logic
│   │   └── package-info.java      # Use cases documentation
│   ├── services/                 # Application Services
│   │   └── package-info.java      # Application services documentation
│   └── dtos/                     # Data Transfer Objects
│       └── package-info.java      # DTOs documentation
├── infrastructure/               # Infrastructure Layer (External Concerns)
│   ├── package-info.java          # Infrastructure layer documentation
│   ├── persistence/              # Repository Implementations
│   │   └── package-info.java      # Persistence documentation
│   ├── config/                  # Configuration Classes
│   │   └── package-info.java      # Configuration documentation
│   └── external/                # External Services (APIs, etc.)
│       └── package-info.java      # External services documentation
└── presentation/                # Presentation Layer (Controllers, UI)
    ├── package-info.java          # Presentation layer documentation
    ├── controllers/             # REST Controllers
    │   └── package-info.java      # Controllers documentation
    ├── middlewares/             # Middlewares/Filters
    │   └── package-info.java      # Middlewares documentation
    └── exception/               # Exception Handlers
        └── package-info.java      # Exception handlers documentation

## Mô tả từng layer:

### 1. Domain Layer (domain/)
- **entities/**: Chứa các business entities, domain models
- **repositories/**: Chứa các interface repository (contracts)
- **services/**: Chứa các domain services, business rules

### 2. Application Layer (application/)
- **usecases/**: Chứa các use cases, orchestrate business logic
- **services/**: Chứa các application services
- **dtos/**: Chứa các Data Transfer Objects cho việc truyền dữ liệu

### 3. Infrastructure Layer (infrastructure/)
- **persistence/**: Chứa implementation của repositories, database access
- **config/**: Chứa các configuration classes (Spring Config, Database Config, etc.)
- **external/**: Chứa các services giao tiếp với external systems

### 4. Presentation Layer (presentation/)
- **controllers/**: Chứa các REST controllers
- **middlewares/**: Chứa các middleware, filters
- **exception/**: Chứa global exception handlers

## Nguyên tắc Dependency:

```
Presentation → Application → Domain ← Infrastructure
```

- **Domain** không phụ thuộc vào layer nào khác
- **Application** chỉ phụ thuộc vào Domain
- **Infrastructure** phụ thuộc vào Domain (implement interfaces)
- **Presentation** phụ thuộc vào Application và Domain

## Lợi ích:

1. **Separation of Concerns**: Mỗi layer có trách nhiệm riêng biệt
2. **Testability**: Dễ dàng unit test từng layer độc lập
3. **Maintainability**: Dễ bảo trì và mở rộng
4. **Flexibility**: Dễ thay đổi implementation mà không ảnh hưởng business logic
5. **Independence**: Business logic không phụ thuộc vào framework hay external systems

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

