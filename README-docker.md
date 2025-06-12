# Docker Setup Instructions for DBDocs

This guide explains how to build and run the DBDocs application using Docker.

## Prerequisites

- Docker installed on your machine
- Docker Compose installed on your machine

## Building and Running with Docker Compose

The easiest way to run the application is with Docker Compose, which will set up both the application and the database.

1. Clone the repository and navigate to the project root:

```bash
git clone <repository-url>
cd dbdocs
```

2. Configure environment variables:

   a. Make a copy of the example environment file:
   ```bash
   cp .env-prod.example .env-prod  # Nếu bạn chưa có file .env-prod
   ```

   b. Edit the `.env-prod` file and update with your actual values:
   ```bash
   nano .env-prod  # hoặc sử dụng editor tùy thích
   ```

   c. Cập nhật các giá trị cho:
   - `GOOGLE_OAUTH2_CLIENT_ID`
   - `GOOGLE_OAUTH2_CLIENT_SECRET`
   - `GITHUB_OAUTH2_CLIENT_ID`
   - `GITHUB_OAUTH2_CLIENT_SECRET`
   - `JWT_SECRET` (nên tạo một secret key mới cho production)

3. Build and start the containers:

```bash
docker-compose up -d
```

4. The application will be available at `http://localhost:8080`

5. To stop the application:

```bash
docker-compose down
```

## Building and Running Manually

If you prefer to build and run the Docker containers manually:

1. Build the application image:

```bash
docker build -t dbdocs:latest .
```

2. Run a PostgreSQL container:

```bash
docker run -d \
  --name dbdocs-db \
  -e POSTGRES_DB=dbdocs \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:15-alpine
```

3. Create a `.env-prod` file with your environment variables, then run the application container:

```bash
docker run -d \
  --name dbdocs-app \
  -p 8080:8080 \
  --env-file .env-prod \
  --link dbdocs-db:db \
  dbdocs:latest
```

## Environment Variables

The application requires the following environment variables in your `.env-prod` file:

| Variable | Description |
|----------|-------------|
| `SPRING_PROFILES_ACTIVE` | Set to `prod` for production environment |
| `DBDOCS_DB_URL` | JDBC URL for the PostgreSQL database |
| `DBDOCS_DB_USERNAME` | Database username |
| `DBDOCS_DB_PASSWORD` | Database password |
| `GOOGLE_OAUTH2_CLIENT_ID` | Google OAuth2 client ID |
| `GOOGLE_OAUTH2_CLIENT_SECRET` | Google OAuth2 client secret |
| `GITHUB_OAUTH2_CLIENT_ID` | GitHub OAuth2 client ID |
| `GITHUB_OAUTH2_CLIENT_SECRET` | GitHub OAuth2 client secret |
| `JWT_SECRET` | Secret key for JWT token generation |
| `JWT_EXPIRATION` | JWT token expiration time in milliseconds |

## Volumes

The Docker Compose setup creates a named volume for PostgreSQL data:

- `postgres_data`: Stores the PostgreSQL database files

## Logs

Application logs are stored in the container. To view logs:

```bash
# View application logs
docker logs dbdocs-app

# Follow application logs
docker logs -f dbdocs-app
```

## Troubleshooting

1. **Database Connection Issues**
   - Ensure the database container is running: `docker ps`
   - Check database logs: `docker logs dbdocs-db`
   - Verify the database URL, username, and password in your `.env-prod` file

2. **Application Startup Issues**
   - Check application logs: `docker logs dbdocs-app`
   - Ensure all required environment variables are set in your `.env-prod` file

3. **OAuth Configuration**
   - Verify that your OAuth credentials are correct in the `.env-prod` file
   - Ensure the redirect URIs are properly configured in the OAuth providers 