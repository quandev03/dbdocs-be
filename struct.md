# Clean Architecture Structure

src/main/java/com/vissoft/vn/dbdocs/
├── DbdocsApplication.java
├── domain/                     # Domain Layer (Core Business Logic)
│   ├── entities/              # Business Entities
│   ├── repositories/          # Repository Interfaces (Contracts)
│   └── services/              # Domain Services
├── application/               # Application Layer (Use Cases)
│   ├── usecases/             # Use Cases / Business Logic
│   ├── services/             # Application Services
│   └── dtos/                 # Data Transfer Objects
├── infrastructure/           # Infrastructure Layer (External Concerns)
│   ├── persistence/          # Repository Implementations
│   ├── config/              # Configuration Classes
│   └── external/            # External Services (APIs, etc.)
└── presentation/            # Presentation Layer (Controllers, UI)
    ├── controllers/         # REST Controllers
    ├── middlewares/         # Middlewares/Filters
    └── exception/           # Exception Handlers

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

