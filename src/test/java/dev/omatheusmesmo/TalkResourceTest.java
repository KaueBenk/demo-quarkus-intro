package dev.omatheusmesmo;

import java.time.LocalDateTime;

import dev.omatheusmesmo.model.Level;
import dev.omatheusmesmo.model.Talk;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class TalkResourceTest {

    @BeforeEach
    @Transactional
    void cleanDatabase() {
        Talk.deleteAll();
    }

    @Test
    void shouldCreateTalk() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "title": "Getting Started with Quarkus",
                            "speaker": "Joao Silva",
                            "startTime": "2026-04-26T14:00:00",
                            "endTime": "2026-04-26T15:00:00",
                            "level": "BEGINNER"
                        }
                        """)
                .when()
                .post("/talks")
                .then()
                .statusCode(201)
                .header("Location", containsString("/talks/"))
                .body("title", is("Getting Started with Quarkus"))
                .body("speaker", is("Joao Silva"))
                .body("startTime", is("2026-04-26T14:00:00"))
                .body("endTime", is("2026-04-26T15:00:00"))
                .body("level", is("BEGINNER"))
                .body("id", notNullValue());
    }

    @Test
    void shouldListAllTalks() {
        createTalk("Quarkus for Beginners", "Maria", LocalDateTime.of(2026, 4, 26, 10, 0), LocalDateTime.of(2026, 4, 26, 10, 45), Level.BEGINNER);
        createTalk("Advanced Quarkus", "Pedro", LocalDateTime.of(2026, 4, 26, 11, 0), LocalDateTime.of(2026, 4, 26, 12, 0), Level.ADVANCED);

        given()
                .when()
                .get("/talks")
                .then()
                .statusCode(200)
                .body("size()", is(2));
    }

    @Test
    void shouldGetTalkById() {
        Long id = createTalk("REST with Quarkus", "Ana", LocalDateTime.of(2026, 4, 26, 14, 0), LocalDateTime.of(2026, 4, 26, 14, 30), Level.INTERMEDIATE);

        given()
                .when()
                .get("/talks/" + id)
                .then()
                .statusCode(200)
                .body("title", is("REST with Quarkus"))
                .body("speaker", is("Ana"));
    }

    @Test
    void shouldReturn404WhenTalkNotFound() {
        given()
                .when()
                .get("/talks/99999")
                .then()
                .statusCode(404);
    }

    @Test
    void shouldUpdateTalk() {
        Long id = createTalk("Old Title", "Carlos", LocalDateTime.of(2026, 4, 26, 9, 0), LocalDateTime.of(2026, 4, 26, 9, 30), Level.BEGINNER);

        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "title": "New Title",
                            "speaker": "Carlos Updated",
                            "startTime": "2026-04-26T10:00:00",
                            "endTime": "2026-04-26T11:30:00",
                            "level": "ADVANCED"
                        }
                        """)
                .when()
                .put("/talks/" + id)
                .then()
                .statusCode(200)
                .body("title", is("New Title"))
                .body("speaker", is("Carlos Updated"))
                .body("startTime", is("2026-04-26T10:00:00"))
                .body("endTime", is("2026-04-26T11:30:00"))
                .body("level", is("ADVANCED"));
    }

    @Test
    void shouldDeleteTalk() {
        Long id = createTalk("To Delete", "Fulano", LocalDateTime.of(2026, 4, 26, 16, 0), LocalDateTime.of(2026, 4, 26, 16, 15), Level.BEGINNER);

        given()
                .when()
                .delete("/talks/" + id)
                .then()
                .statusCode(204);

        given()
                .when()
                .get("/talks/" + id)
                .then()
                .statusCode(404);
    }

    @Test
    void shouldSearchTalksByLevel() {
        createTalk("Intro", "A", LocalDateTime.of(2026, 4, 26, 10, 0), LocalDateTime.of(2026, 4, 26, 10, 30), Level.BEGINNER);
        createTalk("Advanced", "B", LocalDateTime.of(2026, 4, 26, 11, 0), LocalDateTime.of(2026, 4, 26, 12, 0), Level.ADVANCED);
        createTalk("Another Intro", "C", LocalDateTime.of(2026, 4, 26, 14, 0), LocalDateTime.of(2026, 4, 26, 14, 45), Level.BEGINNER);

        given()
                .queryParam("level", "BEGINNER")
                .when()
                .get("/talks/search")
                .then()
                .statusCode(200)
                .body("size()", is(2));
    }

    @Test
    void shouldRejectTalkWithBlankTitle() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "title": "",
                            "speaker": "Joao",
                            "startTime": "2026-04-26T14:00:00",
                            "endTime": "2026-04-26T15:00:00",
                            "level": "BEGINNER"
                        }
                        """)
                .when()
                .post("/talks")
                .then()
                .statusCode(400);
    }

    @Test
    void shouldRejectTalkWithNullStartTime() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "title": "Missing Start Time",
                            "speaker": "Joao",
                            "startTime": null,
                            "endTime": "2026-04-26T15:00:00",
                            "level": "BEGINNER"
                        }
                        """)
                .when()
                .post("/talks")
                .then()
                .statusCode(400);
    }

    @Transactional
    Long createTalk(String title, String speaker, LocalDateTime startTime, LocalDateTime endTime, Level level) {
        Talk t = new Talk(title, speaker, startTime, endTime, level);
        t.persist();
        return t.id;
    }
}
