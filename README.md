# WordCloud Core API

Spring Boot REST API for document upload, processing orchestration, and word statistics.

Receives file uploads, splits text into chunks, publishes them to RabbitMQ for async processing, and serves word statistics to the frontend.

## Prerequisites

- Java 21 (Temurin recommended)
- Running PostgreSQL and RabbitMQ (see below)

## Local Development

### 1. Start infrastructure

```bash
cd ../deployment-config
docker compose -f docker-compose.infra.yml up
```

This starts PostgreSQL on port 5432 and RabbitMQ on port 5672.

### 2. Run the service

```bash
./gradlew bootRun
```

The API starts on http://localhost:8080.

## API Endpoints

| Method | Endpoint                                | Description                 |
|--------|----------------------------------------|-----------------------------|
| POST   | `/api/documents/upload`                | Upload a text file          |
| GET    | `/api/documents/{id}/status`           | Check processing status     |
| GET    | `/api/documents/{id}/statistics`       | Get word frequency data     |
| PUT    | `/api/documents/{docId}/words/{wordId}`| Update a word               |
| DELETE | `/api/documents/{docId}/words/{wordId}`| Delete a word               |

## Environment Variables

| Variable                    | Default                                        | Description              |
|-----------------------------|------------------------------------------------|--------------------------|
| `SPRING_DATASOURCE_URL`    | `jdbc:postgresql://localhost:5432/wordcloud`   | PostgreSQL connection URL|
| `SPRING_DATASOURCE_USERNAME`| `user`                                        | Database username        |
| `SPRING_DATASOURCE_PASSWORD`| `password`                                    | Database password        |
| `SPRING_RABBITMQ_HOST`     | `localhost`                                    | RabbitMQ host            |
| `SPRING_RABBITMQ_PORT`     | `5672`                                         | RabbitMQ port            |
| `CORS_ALLOWED_ORIGINS`     | `http://localhost:5173`                        | Allowed CORS origins     |

## Docker

```bash
docker build -t wordcloud-core-api .
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/wordcloud \
  -e SPRING_RABBITMQ_HOST=host.docker.internal \
  wordcloud-core-api
```

## Full Stack

To run the entire application with one command, see the [deployment-config README](../deployment-config/README.md or https://github.com/Raimpz/wordcloud-deployment-config/blob/main/README.md).
