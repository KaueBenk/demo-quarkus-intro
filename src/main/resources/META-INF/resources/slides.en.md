# Getting Started with Quarkus

## From Zero to Native in 60 Minutes

---

# About Me

- **Matheus Oliveira** — @omatheusmesmo
- Developer @ Act/BMW · Quarkus Contributor
- Blog: **blog.omatheusmesmo.dev**
- Project: `github.com/omatheusmesmo/demo-quarkus-intro`

---

# What Are We Building?

A REST API to manage **Talks**

- Full CRUD (Create, List, Get, Update, Delete)
- PostgreSQL database
- Automatic validation
- OpenAPI/Swagger documentation

---

# What You'll See Today

| Step | Time | Highlight |
|------|------|-----------|
| 1. Project setup | 5 min | Quarkus CLI |
| 2. Dev Mode + Hot Reload | 10 min | Live coding |
| 3. Persistence + Dev Services | 15 min | Panache, PostgreSQL |
| 4. Full CRUD | 10 min | REST, validation |
| 5. Native Build | 10 min | `quarkus build --native` |
| 6. Recap and next steps | 10 min | Comparison |

---

# Why Quarkus?

> "Kubernetes Native Java stack tailor-made for GraalVM and OpenJDK HotSpot"

- **Startup in milliseconds** (not seconds)
- **Memory consumption in MB** (not GB)
- **Live coding** — efficient hot reload
- **Dev Services** — database starts automatically
- **Native build** — one command

---

# Prerequisites

- **Java 17+** — `java -version`
- **Maven 3.9+** — `mvn -version`
- **Quarkus CLI 3.34+** — `quarkus --version`
- **Docker** — Dev Services and native build

---

# Installing the Quarkus CLI

```bash
curl -Ls https://sh.jbang.dev | bash -s - \
trust add https://repo1.maven.org/maven2/io/quarkus/quarkus-cli/

curl -Ls https://sh.jbang.dev | bash -s - \
app install --fresh --force quarkus@quarkusio
```

> Or: `sdk install quarkus` (via SDKMAN!)

---

# Step 1 — Generating the Project

## With Quarkus CLI:

```bash
quarkus create app dev.omatheusmesmo:demo-quarkus-intro \
--extensions='rest-jackson,hibernate-orm-panache,jdbc-postgresql,hibernate-validator,smallrye-openapi'
```

## Or with Maven:

```bash
mvn io.quarkus.platform:quarkus-maven-plugin:create \
-DplatformVersion=3.34.6 \
-DgroupId=dev.omatheusmesmo \
-DartifactId=demo-quarkus-intro \
-Dextensions='rest-jackson,hibernate-orm-panache,jdbc-postgresql,hibernate-validator,smallrye-openapi'
```

---

# Step 1 — Project Structure

Quarkus already generates a sample endpoint:

```
demo-quarkus-intro
├── mvnw
├── mvnw.cmd
├── pom.xml
├── README.md
└── src
    ├── main
    │   ├── docker
    │   │   ├── Dockerfile.jvm
    │   │   ├── Dockerfile.legacy-jar
    │   │   ├── Dockerfile.native
    │   │   └── Dockerfile.native-micro
    │   ├── java
    │   │   └── dev
    │   │       └── omatheusmesmo
    │   │           ├── GreetingResource.java
    │   │           └── MyEntity.java
    │   └── resources
    │       ├── application.properties
    │       └── import.sql
    └── test
        └── java
            └── dev
                └── omatheusmesmo
                    ├── GreetingResourceIT.java
                    └── GreetingResourceTest.java
```

> `GreetingResource.java` is our starting point
> for the hot reload demo in Step 2!
> No `web.xml`, no `beans.xml`, no boilerplate!

---

# Step 1 — Extensions

| Extension | What's it for? |
|-----------|---------------|
| `rest-jackson` | REST + JSON |
| `hibernate-orm-panache` | Active Record persistence |
| `jdbc-postgresql` | PostgreSQL driver |
| `hibernate-validator` | Bean Validation |
| `smallrye-openapi` | Swagger UI |

> Transitive dependencies resolved automatically!

---

# Step 1 — application.properties (dev)

```properties
quarkus.http.port=7070

quarkus.hibernate-orm.physical-naming-strategy=\
org.hibernate.boot.model.naming\
.CamelCaseToUnderscoresNamingStrategy
```

> Dev Services = `drop-and-create` by default in dev/test
>
> `CamelCaseToUnderscoresNamingStrategy`:
> `startTime` → `start_time`

---

# Step 1 — application.properties (prod)

Dev Services handles dev/test. For production:

```properties
%prod.quarkus.datasource.db-kind=postgresql
%prod.quarkus.datasource.jdbc.url=\
jdbc:postgresql://localhost:5432/demo
%prod.quarkus.datasource.username=quarkus
%prod.quarkus.datasource.password=quarkus
```

> `%prod.` = this config **only** applies in production.

---

# Step 2 — Starting Dev Mode

```bash
quarkus dev
```

### Or with Maven:

```bash
./mvnw quarkus:dev
```

**What happens:**

1. App starts in **~1 second** on port 7070
2. Swagger UI at `/q/swagger-ui`
3. Dev Services starts PostgreSQL
4. **These slides** served by Quarkus itself!

> Access `http://localhost:7070/presentation.html`

---

# Step 2 — Dev UI

Access: **http://localhost:7070/q/dev**

Everything in one place:

- **Extensions** — dependencies and versions
- **Configuration** — all properties
- **Datasource** — database status
- **Health** — automatic checks
- **OpenAPI** — schema and Swagger

> **Control panel** for your app in the browser!

---

# Step 2 — Hot Reload / Live Coding

## Change the code, save, and it's live. No restart!

### First demo — the generated GreetingResource:

```bash
curl http://localhost:7070/hello
# "Hello from Quarkus REST"
```

### Now, modify it live:

```java
return "Hello from Quarkus! 🚀";
```

**Save** → access again → **Changed instantly!**

> Spring Boot: ~10s restart. Quarkus: **<1s**.

---

# Step 2 — Why It Matters

| | Quarkus | Spring Boot |
|---|---|---|
| Code change | < 1s | 5-15s |
| Config change | Hot Reload | Restart |
| New endpoint | Hot Reload | Restart |
| Compilation error | In browser | In log |

> Quarkus analyzes **what changed**
> and only reloads what's necessary.

---

# Step 3 — What Are Dev Services?

> "Docker Compose? Never heard of it."

When you add `jdbc-postgresql`, Quarkus:

1. **Detects** there's no datasource
2. **Starts a PostgreSQL container** automatically
3. **Configures** URL, username, and password
4. **Stops the container** on shutdown

> Zero configuration needed!
> Schema managed automatically in dev/test.

---

# Step 3 — Dev Services in Action

```bash
quarkus dev
```

You'll see in the log:

```
(build-2) Setting quarkus.hibernate-orm.schema-management.strategy=drop-and-create
to initialize Dev Services managed database
(ForkJoinPool.commonPool-worker-5) Dev Services for default datasource (postgresql) started
- container ID is 6e525babdb94
__ ____ __ _____ ___ __ ____ ______
--/ __ \/ / / / _ | / _ \/ //_/ / / / __/
-/ /_/ / /_/ / __ |/ , _/ ,< / /_/ /\ \
--\___\_\____/_/ |_/_/|_/_/|_|\____/___/
(Quarkus Main Thread) demo-quarkus-intro 1.0.0-SNAPSHOT on JVM (powered by Quarkus 3.34.6)
started in 6.620s. Listening on: http://localhost:7071
(Quarkus Main Thread) Profile dev activated. Live Coding activated.
(Quarkus Main Thread) Installed features: [agroal, cdi, compose, hibernate-orm, hibernate-orm-panache,
hibernate-validator, jdbc-postgresql, narayana-jta, rest, rest-jackson,
smallrye-context-propagation, smallrye-openapi, swagger-ui, vertx]
```

> **No docker-compose, no installing PostgreSQL!**

---

# Step 3 — Model: Level

```java
package dev.omatheusmesmo.model;

public enum Level { BEGINNER, INTERMEDIATE, ADVANCED }
```

---

# Step 3 — Model: Talk

```java
package dev.omatheusmesmo.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class Talk extends PanacheEntity {

    @Column(nullable = false)
    public String title;

    @Column(nullable = false)
    public String speaker;

    @Column(nullable = false)
    public LocalDateTime startTime;

    @Column(nullable = false)
    public LocalDateTime endTime;

    @Column(nullable = false)
    public Level level;

    public Talk() {}

    public Talk(String title, String speaker, LocalDateTime startTime, LocalDateTime endTime, Level level) {
        this.title = title;
        this.speaker = speaker;
        this.startTime = startTime;
        this.endTime = endTime;
        this.level = level;
    }

    public void updateFrom(String title, String speaker, LocalDateTime startTime, LocalDateTime endTime, Level level) {
        this.title = title;
        this.speaker = speaker;
        this.startTime = startTime;
        this.endTime = endTime;
        this.level = level;
    }

    public static Talk findByTitle(String title) {
        return find("title", title).firstResult();
    }

    public static List<Talk> findByLevel(Level level) {
        return list("level", level);
    }
}
```

> **PanacheEntity** gives us `id`, `persist()`,
> `findById()`, `listAll()` for free!
> No-arg constructor required by Hibernate.

---

# Step 3 — Panache: Before and After

### Without Panache (plain JPA):

```java
@PersistenceContext
EntityManager em;

public List<Talk> findAll() {
    return em.createQuery(
        "SELECT t FROM Talk t", Talk.class)
        .getResultList();
}
```

### With Panache:

```java
Talk.listAll();
Talk.findById(id);
talk.persist();
talk.delete();
Talk.count();
```

> Less code, more readable, zero boilerplate!

---

# Step 3 — DTO: TalkRequest

```java
package dev.omatheusmesmo.dto;

import java.time.LocalDateTime;
import dev.omatheusmesmo.model.Level;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TalkRequest(
    @NotBlank(message = "Title is required")
    String title,

    @NotBlank(message = "Speaker is required")
    String speaker,

    @NotNull(message = "Start time is required")
    LocalDateTime startTime,

    @NotNull(message = "End time is required")
    LocalDateTime endTime,

    @NotNull(message = "Level is required")
    Level level
) {}
```

> Declarative validation — no `if/else`!

---

# Step 3 — DTO: TalkResponse

```java
package dev.omatheusmesmo.dto;

import java.time.LocalDateTime;
import dev.omatheusmesmo.model.Level;
import dev.omatheusmesmo.model.Talk;

public record TalkResponse(
    Long id, String title, String speaker,
    LocalDateTime startTime,
    LocalDateTime endTime, Level level
) {
    public static TalkResponse from(Talk t) {
        return new TalkResponse(
            t.id, t.title, t.speaker,
            t.startTime, t.endTime, t.level
        );
    }
}
```

> Never expose entities in the API — use DTOs!

---

# Step 4 — Full CRUD

```java
package dev.omatheusmesmo.resource;

import java.net.URI;
import java.util.List;

import org.jboss.resteasy.reactive.RestQuery;

import dev.omatheusmesmo.dto.TalkRequest;
import dev.omatheusmesmo.dto.TalkResponse;
import dev.omatheusmesmo.model.Level;
import dev.omatheusmesmo.model.Talk;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

@Path("/talks")
public class TalkResource {

    // @GET
    // public List<TalkResponse> list() { ... }

    // @GET @Path("/{id}")
    // public TalkResponse get(@PathParam("id") Long id) { ... }

    // @GET @Path("/search")
    // public List<TalkResponse> search(
    //     @RestQuery Level level) { ... }

    // @POST @Transactional
    // public Response create(@Valid TalkRequest req) { ... }

    // @PUT @Path("/{id}") @Transactional
    // public TalkResponse update(@PathParam("id") Long id,
    //     @Valid TalkRequest req) { ... }

    // @DELETE @Path("/{id}") @Transactional
    // public void delete(@PathParam("id") Long id) { ... }
}
```

---

# Step 4 — Resource: POST

```java
@POST
@Transactional
public Response create(@Valid TalkRequest req) {
    Talk t = new Talk(req.title(), req.speaker(),
        req.startTime(), req.endTime(), req.level());
    t.persist();
    return Response
        .created(URI.create("/talks/" + t.id))
        .entity(TalkResponse.from(t))
        .build();
}
```

> `Response` for **201 Created** + `Location`.
> `@Transactional` on writes.
> In production, extract to a **Service**.

---

# Step 4 — POST: Testing

```bash
curl -X POST http://localhost:7070/talks \
-H "Content-Type: application/json" \
-d '{"title":"Getting Started with Quarkus","speaker":"Matheus Oliveira","startTime":"2026-04-26T14:00:00","endTime":"2026-04-26T15:00:00","level":"BEGINNER"}'
```

### Response (201):

```json
{"id":1,"title":"Getting Started with Quarkus",
"speaker":"Matheus Oliveira",
"startTime":"2026-04-26T14:00:00",
"endTime":"2026-04-26T15:00:00","level":"BEGINNER"}
```

---

# Step 4 — Resource: GET

```java
package dev.omatheusmesmo.resource;

import java.util.List;
import dev.omatheusmesmo.dto.TalkResponse;
import dev.omatheusmesmo.model.Talk;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/talks")
public class TalkResource {

    @GET
    public List<TalkResponse> list() {
        return Talk.<Talk>listAll().stream()
            .map(TalkResponse::from).toList();
    }

    @GET
    @Path("/{id}")
    public TalkResponse get(@PathParam("id") Long id) {
        Talk talk = Talk.findById(id);
        if (talk == null) throw new NotFoundException();
        return TalkResponse.from(talk);
    }
}
```

> Direct DTO = automatic 200. Avoid `Response.ok(entity)` — breaks native build
> without `@RegisterForReflection`!

---

# Step 4 — GET: Testing

```bash
# List all
curl http://localhost:7070/talks

# Get by ID
curl http://localhost:7070/talks/1
```

---

# Step 4 — Resource: Search

```java
@GET
@Path("/search")
public List<TalkResponse> search(
    @RestQuery Level level) {
    return Talk.findByLevel(level).stream()
        .map(TalkResponse::from)
        .toList();
}
```

```bash
curl "http://localhost:7070/talks/search?level=BEGINNER"
```

> `@RestQuery` = automatic query param.
> Search logic in the model — Resource stays clean.

---

# Step 4 — Resource: PUT

```java
@PUT
@Path("/{id}")
@Transactional
public TalkResponse update(@PathParam("id") Long id,
    @Valid TalkRequest req) {
    Talk talk = Talk.findById(id);
    if (talk == null) throw new NotFoundException();
    talk.updateFrom(req.title(), req.speaker(),
        req.startTime(), req.endTime(), req.level());
    return TalkResponse.from(talk);
}
```

> Domain logic in the entity, Resource stays clean!

---

# Step 4 — PUT: Testing

```bash
curl -X PUT http://localhost:7070/talks/1 \
-H "Content-Type: application/json" \
-d '{"title":"Advancing with Quarkus","speaker":"Matheus Oliveira","startTime":"2026-04-26T14:00:00","endTime":"2026-04-26T15:00:00","level":"INTERMEDIATE"}'
```

### Response (200): title and level updated

---

# Step 4 — Resource: DELETE

```java
@DELETE
@Path("/{id}")
@Transactional
public void delete(@PathParam("id") Long id) {
    Talk talk = Talk.findById(id);
    if (talk == null) throw new NotFoundException();
    talk.delete();
}
```

> `void` = **204 No Content** automatically.

---

# Step 4 — DELETE: Testing

```bash
curl -X DELETE http://localhost:7070/talks/1 -v
```

### Response: `204 No Content`

```bash
# Confirm: list returns empty
curl http://localhost:7070/talks
```

---

# Step 4 — Validation in Action

### Invalid request:

```bash
curl -X POST http://localhost:7070/talks \
-H "Content-Type: application/json" \
-d '{"title":"","speaker":"John","startTime":null,"endTime":"2026-04-26T15:00:00","level":"BEGINNER"}'
```

### Response (400):

```json
{
  "violations": [
    { "field": "title", "message": "Title is required" },
    { "field": "startTime", "message": "Start time is required" }
  ]
}
```

> Declarative validation. Automatic JSON!

---

# Step 5 — Native Build

GraalVM compiles Java to a **native binary**:

- **No JVM** — no Java installation needed
- **Startup in milliseconds**
- **Minimal memory consumption**
- **Tiny Docker image**

---

# Step 5 — Native Build: Commands

### With Quarkus CLI:

```bash
quarkus build --native
```

### Or with Maven:

```bash
./mvnw package -Dnative
```

### No GraalVM? Use Docker:

```bash
./mvnw package -Dnative \
-Dquarkus.native.container-build=true
```

> Docker: `docker build -f src/main/docker/Dockerfile.native-micro .`

---

# Step 5 — Demo Time!

### 1. Start PostgreSQL (Dev Services doesn't work in prod):

```bash
docker run -d --name demo-postgres \
-e POSTGRES_USER=quarkus \
-e POSTGRES_PASSWORD=quarkus \
-e POSTGRES_DB=demo \
-p 5432:5432 postgres:17
```

### 2. Run the native binary:

```bash
./target/demo-quarkus-intro-1.0.0-SNAPSHOT-runner
```

```
__ ____ __ _____ ___ __ ____ ______
--/ __ \/ / / / _ | / _ \/ //_/ / / / __/
-/ /_/ / /_/ / __ |/ , _/ ,< / /_/ /\ \
--\___\_\____/_/ |_/_/|_/_/|_|\____/___/
Started in 0.042s
```

> No JVM, no warmup, no slow startup!

---

# Step 5 — Startup Comparison (Latest Benchmarks)

| | Spring Boot | Quarkus |
|---|---|---|
| Startup (JVM) | 6.6s | 2.9s |
| Startup (Native) | 0.6s | 0.03s |
| Memory (JVM) | 583MB | 269MB |
| Memory (Native) | ~110MB | ~23MB |
| Throughput | 7,238 tps | 19,255 tps |

> Source: quarkus.io/blog/new-benchmarks — **2.7x more throughput, 2.3x faster, half the memory**

---

# Step 6 — Swagger UI

Access: **http://localhost:7070/q/swagger-ui**

- Interactive documentation
- Test endpoints in the browser
- OpenAPI schema at `/q/openapi`

> **Zero configuration** — just add the extension!

---

# Step 6 — Final Comparison

| | Spring Boot | Quarkus |
|---|---|---|
| Startup (JVM) | ~6.6s | ~2.9s |
| Startup (Native) | ~0.6s | ~0.03s |
| Memory (JVM) | ~583MB | ~269MB |
| Throughput | 7,238 tps | 19,255 tps |
| Hot Reload | 5-15s | <1s |
| Dev Services | No | Automatic |
| Native Build | GraalVM AOT | Out of the box |
| Dev UI | No | `/q/dev` |

---

# Summary

In **less than 1 hour**, from zero:

- REST API with full CRUD
- PostgreSQL **without installing anything**
- Instant Hot Reload
- Automatic validation
- Automatic Swagger
- Native build with **one command**

---

# Next Steps

- **Security** — `quarkus-oidc`
- **Observability** — Micrometer + Prometheus
- **Reactive** — Hibernate Reactive
- **Messaging** — Kafka, RabbitMQ
- **DB Migration** — Flyway, Liquibase
- **Testing** — `@QuarkusTest`
- **Extensions** — code.quarkus.io

---

# Reference Commands

```bash
quarkus create app dev.omatheusmesmo:demo-quarkus-intro \
--extensions='rest-jackson,hibernate-orm-panache,jdbc-postgresql,hibernate-validator,smallrye-openapi'

quarkus dev                 # Dev mode
quarkus test                # Run tests
quarkus build               # JVM build
quarkus build --native      # Native build
quarkus extension add oidc  # Add extension
```

---

# Resources

- **quarkus.io** — Official site
- **guides.quarkus.io** — Complete guides
- **code.quarkus.io** — Project generator
- **quarkus.io/blog/new-benchmarks** — Updated benchmarks
- **github.com/quarkusio/spring-quarkus-perf-comparison** — Benchmarks repo
- **Quarkus Zulip Chat** — Community
- **github.com/quarkusio/quarkus** — Source code

---

# Thank You!

## Questions?

- **Project:** `github.com/omatheusmesmo/demo-quarkus-intro`
- **GitHub:** `github.com/omatheusmesmo`
- **X:** `@omatheusmesmo_`
- **Blog:** `blog.omatheusmesmo.dev`
- **LinkedIn:** `linkedin.com/in/omatheusmesmo`

> *"Write reactive and imperative code with the same API,
> compile to native, run everywhere."*
