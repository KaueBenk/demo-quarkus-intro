# demo-quarkus-intro

Getting Started with Quarkus — Step-by-step demo from zero to native build.

A REST API to manage Talks, showcasing Quarkus key features.

## Features

- Full Talk CRUD (create, list, get, update, delete)
- Persistence with Hibernate ORM + Panache (Active Record)
- PostgreSQL via Dev Services (zero config in dev/test)
- Bean Validation
- Auto-generated OpenAPI/Swagger UI
- Native build with one command

## Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/talks` | List all talks |
| GET | `/talks/{id}` | Get talk by ID |
| GET | `/talks/search?level=BEGINNER` | Search talks by level |
| POST | `/talks` | Create a new talk |
| PUT | `/talks/{id}` | Update a talk |
| DELETE | `/talks/{id}` | Delete a talk |

## Running in dev mode

```bash
quarkus dev
```

Or with Maven:

```bash
./mvnw quarkus:dev
```

Access: http://localhost:7070

Swagger UI: http://localhost:7070/q/swagger-ui

## Running the tests

```bash
quarkus test
```

Or:

```bash
./mvnw test
```

## JVM Build

```bash
quarkus build
```

## Native Build

```bash
quarkus build --native
```

Or with Docker (no GraalVM required):

```bash
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

## Presentation

The talk slides are in `src/main/resources/META-INF/resources/` and served by Quarkus itself:

```bash
quarkus dev
# Access http://localhost:7070/presentation.html
```

Language toggle available: PT-BR / EN.

## Quarkus Guides

- [Hibernate ORM with Panache](https://quarkus.io/guides/hibernate-orm-panache)
- [REST Jackson](https://quarkus.io/guides/rest#json-serialisation)
- [Hibernate Validator](https://quarkus.io/guides/validation)
- [SmallRye OpenAPI](https://quarkus.io/guides/openapi-swaggerui)
- [Dev Services](https://quarkus.io/guides/databases-dev-services)
- [Building Native Executables](https://quarkus.io/guides/maven-tooling)
