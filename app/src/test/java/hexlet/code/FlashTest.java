package hexlet.code;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import kong.unirest.Unirest;
import io.javalin.Javalin;

import java.io.IOException;
import java.sql.SQLException;

class FlashTest {
    private Javalin app;
    private String baseUrl;

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "7070");
        return Integer.valueOf(port);
    }

    @BeforeEach
    public void beforeAll() throws SQLException, IOException {
        App.setTestingMode();
        app = App.getApp();
        int port = getPort();
        app.start(port);

        baseUrl = "http://localhost:" + port;
    }

    @AfterEach
    public void afterAll() {
        app.stop();
    }

    @Test
    void testFlashMessage() {
        // Проверка при добавлении url
        var requestBody = "url=https%3A%2F%2Fgithub.com%3A443%2Ffaraway10%2Fjava-project-72";
        var response = Unirest
                .post(baseUrl + "/urls")
                .body(requestBody)
                .asEmpty();

        assertThat(response.getStatus()).isEqualTo(302);

        var response2 = Unirest
                .get(baseUrl + "/urls")
                .asString();

        var content = response2.getBody();
        assertThat(content).contains("Страница успешно добавлена");
        assertThat(content).contains("alert alert-success");

        // Проверка срабатывания на добавленный ранее url
        response = Unirest
                .post(baseUrl + "/urls")
                .body(requestBody)
                .asEmpty();

        assertThat(response.getStatus()).isEqualTo(302);

        response2 = Unirest
                .get(baseUrl + "/urls")
                .asString();

        content = response2.getBody();
        assertThat(content).contains("Страница уже существует");
        assertThat(content).contains("alert alert-info");

        // Проверка невалидного url
        requestBody = "url=bad_url";
        response = Unirest
                .post(baseUrl + "/urls")
                .body(requestBody)
                .asEmpty();

        assertThat(response.getStatus()).isEqualTo(302);

        response2 = Unirest
                .get(baseUrl + "/urls")
                .asString();

        content = response2.getBody();
        assertThat(content).contains("Некорректный URL");
        assertThat(content).contains("alert alert-danger");
    }
}
