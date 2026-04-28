# Primeiros Passos com Quarkus

## Do Zero ao Nativo em 60 Minutos

---

# Sobre Mim

- **Matheus Oliveira** — @omatheusmesmo
- Developer @ Act/BMW · Quarkus Contributor
- Blog: **blog.omatheusmesmo.dev**
- Projeto: `github.com/omatheusmesmo/demo-quarkus-intro`

---

# O que vamos construir?

Uma API REST para gerenciar **Talks**

- CRUD completo (Create, List, Get, Update, Delete)
- Banco de dados PostgreSQL
- Validação automática
- Documentação OpenAPI/Swagger

---

# O que você vai ver hoje

| Etapa | Tempo | Destaque |
|-------|-------|----------|
| 1. Setup do projeto | 5 min | Quarkus CLI |
| 2. Dev Mode + Hot Reload | 10 min | Live coding |
| 3. Persistência + Dev Services | 15 min | Panache, PostgreSQL |
| 4. CRUD completo | 10 min | REST, validação |
| 5. Build Nativo | 10 min | `quarkus build --native` |
| 6. Recap e próximos passos | 10 min | Comparação |

---

# Por que Quarkus?

> "Kubernetes Native Java stack tailor-made for GraalVM and OpenJDK HotSpot"

- **Startup em milissegundos** (não segundos)
- **Consumo de memória em MB** (não GB)
- **Live coding** — hot reload eficiente
- **Dev Services** — banco sobe sozinho
- **Build nativo** — um comando

---

# Pré-requisitos

- **Java 17+** — `java -version`
- **Maven 3.9+** — `mvn -version`
- **Quarkus CLI 3.34+** — `quarkus --version`
- **Docker** — Dev Services e build nativo

---

# Instalando o Quarkus CLI

```bash
curl -Ls https://sh.jbang.dev | bash -s - \
  trust add https://repo1.maven.org/maven2/io/quarkus/quarkus-cli/

curl -Ls https://sh.jbang.dev | bash -s - \
  app install --fresh --force quarkus@quarkusio
```

> Ou: `sdk install quarkus` (via SDKMAN!)

---

# Etapa 1 — Gerando o Projeto

## Com Quarkus CLI:

```bash
quarkus create app dev.omatheusmesmo:demo-quarkus-intro \
--extensions='rest-jackson,hibernate-orm-panache,jdbc-postgresql,hibernate-validator,smallrye-openapi'
```

## Ou com Maven:

```bash
mvn io.quarkus.platform:quarkus-maven-plugin:create \
-DplatformVersion=3.34.6 \
-DgroupId=dev.omatheusmesmo \
-DartifactId=demo-quarkus-intro \
-Dextensions='rest-jackson,hibernate-orm-panache,jdbc-postgresql,hibernate-validator,smallrye-openapi'
```

---

# Etapa 1 — Estrutura do Projeto

O Quarkus já gera um endpoint de exemplo:

```
demo-quarkus-intro
├── mvnw
├── mvnw.cmd
├── pom.xml
├── README.md
└── src
    ├── main
    │   ├── docker
    │   │   ├── Dockerfile.jvm
    │   │   ├── Dockerfile.legacy-jar
    │   │   ├── Dockerfile.native
    │   │   └── Dockerfile.native-micro
    │   ├── java
    │   │   └── dev
    │   │       └── omatheusmesmo
    │   │           ├── GreetingResource.java
    │   │           └── MyEntity.java
    │   └── resources
    │       ├── application.properties
    │       └── import.sql
    └── test
        └── java
            └── dev
                └── omatheusmesmo
                    ├── GreetingResourceIT.java
                    └── GreetingResourceTest.java
```

> `GreetingResource.java` é nosso ponto de partida
> para o hot reload na Etapa 2!
> Sem `web.xml`, sem `beans.xml`, sem boilerplate!

---

# Etapa 1 — Extensões

| Extensão | Para que serve? |
|----------|----------------|
| `rest-jackson` | REST + JSON |
| `hibernate-orm-panache` | Persistência Active Record |
| `jdbc-postgresql` | Driver PostgreSQL |
| `hibernate-validator` | Bean Validation |
| `smallrye-openapi` | Swagger UI |

> Dependências transitivas resolvidas automaticamente!

---

# Etapa 1 — application.properties (dev)

```properties
quarkus.http.port=7070

quarkus.hibernate-orm.physical-naming-strategy=\
org.hibernate.boot.model.naming\
.CamelCaseToUnderscoresNamingStrategy
```

> Dev Services = `drop-and-create` por padrão em dev/test
>
> `CamelCaseToUnderscoresNamingStrategy`:
> `startTime` → `start_time`

---

# Etapa 1 — application.properties (prod)

Dev Services cuida de dev/test. Para produção:

```properties
%prod.quarkus.datasource.db-kind=postgresql
%prod.quarkus.datasource.jdbc.url=\
jdbc:postgresql://localhost:5432/demo
%prod.quarkus.datasource.username=quarkus
%prod.quarkus.datasource.password=quarkus
```

> `%prod.` = esta config **só** aplica em produção.

---

# Etapa 2 — Subindo o Dev Mode

```bash
quarkus dev
```

### Ou com Maven:

```bash
./mvnw quarkus:dev
```

**O que acontece:**

1. App sobe em **~1 segundo** na porta 7070
2. Swagger UI em `/q/swagger-ui`
3. Dev Services sobe PostgreSQL
4. **Estes slides** servidos pelo próprio Quarkus!

> Acesse `http://localhost:7070/presentation.html`

---

# Etapa 2 — Dev UI

Acesse: **http://localhost:7070/q/dev**

Tudo em um lugar:

- **Extensions** — dependências e versões
- **Configuration** — todas as properties
- **Datasource** — status do banco
- **Health** — checks automático
- **OpenAPI** — schema e Swagger

> **Painel de controle** do seu app no browser!

---

# Etapa 2 — Hot Reload / Live Coding

## Mude o código, salve, e já está ativo. Sem restart!

### Primeiro demo — o GreetingResource gerado:

```bash
curl http://localhost:7070/hello
# "Hello from Quarkus REST"
```

### Agora, modifique ao vivo:

```java
return "Hello from Quarkus! 🚀";
```

**Salve** → acesse novamente → **Mudou instantaneamente!**

> Spring Boot: ~10s restart. Quarkus: **<1s**.

---

# Etapa 2 — Por que importa?

| | Quarkus | Spring Boot |
|---|---|---|
| Mudança de código | < 1s | 5-15s |
| Mudança de config | Hot Reload | Restart |
| Novo endpoint | Hot Reload | Restart |
| Erro de compilação | No browser | No log |

> O Quarkus analisa **o que mudou**
> e só recarrega o necessário.

---

# Etapa 3 — O que são Dev Services?

> "Docker Compose? Não conheço."

Ao adicionar `jdbc-postgresql`, o Quarkus:

1. **Detecta** que não há datasource
2. **Sobe container PostgreSQL** automático
3. **Configura** URL, usuário e senha
4. **Para o container** ao desligar

> Zero configuração necessária!
> Schema gerenciado automaticamente em dev/test.

---

# Etapa 3 — Dev Services em ação

```bash
quarkus dev
```

Você verá no log:

```
(build-2) Setting quarkus.hibernate-orm.schema-management.strategy=drop-and-create
            to initialize Dev Services managed database
(ForkJoinPool.commonPool-worker-5) Dev Services for default datasource (postgresql) started
            - container ID is 6e525babdb94
__  ____  __  _____   ___  __ ____  ______
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

> **Sem docker-compose, sem instalar PostgreSQL!**

---

# Etapa 3 — Modelo: Level

```java
package dev.omatheusmesmo.model;

public enum Level {
    BEGINNER, INTERMEDIATE, ADVANCED
}
```

---

# Etapa 3 — Modelo: Talk

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

> **PanacheEntity** nos dá `id`, `persist()`,
> `findById()`, `listAll()` de graça!
> Construtor vazio exigido pelo Hibernate.

---

# Etapa 3 — Panache: Antes e Depois

### Sem Panache (JPA puro):

```java
@PersistenceContext
EntityManager em;

public List<Talk> findAll() {
    return em.createQuery(
        "SELECT t FROM Talk t", Talk.class)
        .getResultList();
}
```

### Com Panache:

```java
Talk.listAll();
Talk.findById(id);
talk.persist();
talk.delete();
Talk.count();
```

> Menos código, mais legível, zero boilerplate!

---

# Etapa 3 — DTO: TalkRequest

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

> Validação declarativa — sem `if/else`!

---

# Etapa 3 — DTO: TalkResponse

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

> Nunca exponha entidades na API — use DTOs!

---

# Etapa 4 — CRUD Completo

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
    // @RestQuery Level level) { ... }

    // @POST @Transactional
    // public Response create(@Valid TalkRequest req) { ... }

    // @PUT @Path("/{id}") @Transactional
    // public TalkResponse update(@PathParam("id") Long id,
    // @Valid TalkRequest req) { ... }

    // @DELETE @Path("/{id}") @Transactional
    // public void delete(@PathParam("id") Long id) { ... }
}
```

---

# Etapa 4 — Resource: POST

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

> `Response` para **201 Created** + `Location`.
> `@Transactional` em escrita.
> Em produção, extraia pra **Service**.

---

# Etapa 4 — POST: Testando

```bash
curl -X POST http://localhost:7070/talks \
  -H "Content-Type: application/json" \
  -d '{"title":"Primeiros Passos com Quarkus","speaker":"Matheus Oliveira","startTime":"2026-04-26T14:00:00","endTime":"2026-04-26T15:00:00","level":"BEGINNER"}'
```

### Resposta (201):

```json
{"id":1,"title":"Primeiros Passos com Quarkus",
 "speaker":"Matheus Oliveira",
 "startTime":"2026-04-26T14:00:00",
 "endTime":"2026-04-26T15:00:00","level":"BEGINNER"}
```

---

# Etapa 4 — Resource: GET

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

> DTO direto = 200 automático. Evite `Response.ok(entity)` — quebra native build
> sem `@RegisterForReflection`!

---

# Etapa 4 — GET: Testando

```bash
# Listar todos
curl http://localhost:7070/talks

# Buscar por ID
curl http://localhost:7070/talks/1
```

---

# Etapa 4 — Resource: Search

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

> `@RestQuery` = query param automático.
> Lógica de busca no modelo — Resource fica limpo.

---

# Etapa 4 — Resource: PUT

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

> Domínio na entidade, Resource limpo!

---

# Etapa 4 — PUT: Testando

```bash
curl -X PUT http://localhost:7070/talks/1 \
  -H "Content-Type: application/json" \
  -d '{"title":"Avancando com Quarkus","speaker":"Matheus Oliveira","startTime":"2026-04-26T14:00:00","endTime":"2026-04-26T15:00:00","level":"INTERMEDIATE"}'
```

### Resposta (200): título e nível alterados

---

# Etapa 4 — Resource: DELETE

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

> `void` = **204 No Content** automático.

---

# Etapa 4 — DELETE: Testando

```bash
curl -X DELETE http://localhost:7070/talks/1 -v
```

### Resposta: `204 No Content`

```bash
# Confirmar: lista volta vazia
curl http://localhost:7070/talks
```

---

# Etapa 4 — Validação em Ação

### Request inválido:

```bash
curl -X POST http://localhost:7070/talks \
  -H "Content-Type: application/json" \
  -d '{"title":"","speaker":"Joao","startTime":null,"endTime":"2026-04-26T15:00:00","level":"BEGINNER"}'
```

### Resposta (400):

```json
{
    "violations": [
        { "field": "title", "message": "Title is required" },
        { "field": "startTime", "message": "Start time is required" }
    ]
}
```

> Validação declarativa. JSON automático!

---

# Etapa 5 — Build Nativo

GraalVM compila Java para **binário nativo**:

- **Sem JVM** — não precisa de Java instalado
- **Startup em milissegundos**
- **Consumo de memória mínimo**
- **Imagem Docker minúscula**

---

# Etapa 5 — Build Nativo: Comandos

### Com Quarkus CLI:

```bash
quarkus build --native
```

### Ou com Maven:

```bash
./mvnw package -Dnative
```

### Sem GraalVM? Use Docker:

```bash
./mvnw package -Dnative \
-Dquarkus.native.container-build=true
```

> Docker: `docker build -f src/main/docker/Dockerfile.native-micro .`

---

# Etapa 5 — Demo Time!

### 1. Suba o PostgreSQL (Dev Services não roda em prod):

```bash
docker run -d --name demo-postgres \
  -e POSTGRES_USER=quarkus \
  -e POSTGRES_PASSWORD=quarkus \
  -e POSTGRES_DB=demo \
  -p 5432:5432 postgres:17
```

### 2. Rode o binário nativo:

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

> Sem JVM, sem warmup, sem startup lento!

---

# Etapa 5 — Comparação de Startup (Últimos benchmarks)

| | Spring Boot | Quarkus |
|---|---|---|
| Startup (JVM) | 6.6s | 2.9s |
| Startup (Native) | 0.6s | 0.03s |
| Memória (JVM) | 583MB | 269MB |
| Memória (Native) | ~110MB | ~23MB |
| Throughput | 7.238 tps | 19.255 tps |

> Fonte: quarkus.io/blog/new-benchmarks — **2.7x mais throughput, 2.3x mais rápido, metade da memória**

---

# Etapa 6 — Swagger UI

Acesse: **http://localhost:7070/q/swagger-ui**

- Documentação interativa
- Teste de endpoints no browser
- Schema OpenAPI em `/q/openapi`

> **Zero configuração** — basta adicionar a extensão!

---

# Etapa 6 — Comparação Final

| | Spring Boot | Quarkus |
|---|---|---|
| Startup (JVM) | ~6.6s | ~2.9s |
| Startup (Native) | ~0.6s | ~0.03s |
| Memória (JVM) | ~583MB | ~269MB |
| Throughput | 7.238 tps | 19.255 tps |
| Hot Reload | 5-15s | <1s |
| Dev Services | Não | Automático |
| Build Nativo | GraalVM AOT | Out of the box |
| Dev UI | Não | `/q/dev` |

---

# Resumo

Em **menos de 1 hora**, do zero:

- API REST com CRUD completo
- PostgreSQL **sem instalar nada**
- Hot Reload instantâneo
- Validação automática
- Swagger automático
- Build nativo com **um comando**

---

# Próximos Passos

- **Segurança** — `quarkus-oidc`
- **Observabilidade** — Micrometer + Prometheus
- **Reativo** — Hibernate Reactive
- **Mensageria** — Kafka, RabbitMQ
- **Migração de DB** — Flyway, Liquibase
- **Testes** — `@QuarkusTest`
- **Extensions** — code.quarkus.io

---

# Comandos de Referência

```bash
quarkus create app dev.omatheusmesmo:demo-quarkus-intro \
--extensions='rest-jackson,hibernate-orm-panache,jdbc-postgresql,hibernate-validator,smallrye-openapi'

quarkus dev                # Dev mode
quarkus test               # Rodar testes
quarkus build # Build JVM
quarkus build --native # Build nativo
quarkus extension add oidc # Adicionar extensão
```

---

# Recursos

- **quarkus.io** — Site oficial
- **guides.quarkus.io** — Guias completos
- **code.quarkus.io** — Gerador de projetos
- **quarkus.io/blog/new-benchmarks** — Benchmarks atualizados
- **github.com/quarkusio/spring-quarkus-perf-comparison** — Repo dos benchmarks
- **Quarkus Zulip Chat** — Comunidade
- **github.com/quarkusio/quarkus** — Código fonte

---

# Obrigado!

## Perguntas?

- **Projeto:** `github.com/omatheusmesmo/demo-quarkus-intro`
- **GitHub:** `github.com/omatheusmesmo`
- **X:** `@omatheusmesmo_`
- **Blog:** `blog.omatheusmesmo.dev`
- **Linkedin:** `linkedin.com/in/omatheusmesmo`

> *"Write reactive and imperative code with the same API,
> compile to native, run everywhere."*
