package hexlet.code;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import hexlet.code.repository.UrlCheckRepository;
import kong.unirest.Unirest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.apache.commons.lang3.StringUtils;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

public class AppTest {
    private Javalin app;
    private static MockWebServer mockServer;
    private static String mockUrl;

    // Без использования этого метода на Windows в некоторых тестах сбивается кодировка кириллицы
    private static String strToUtf8(String str) {
        return new String(str.getBytes(), UTF_8);
    }

    private static Path getFixturePath(String fileName) {
        return Paths.get("src", "test", "resources", "fixtures", fileName)
                .toAbsolutePath().normalize();
    }

    private static String readFixture(String fileName) throws Exception {
        Path filePath = getFixturePath(fileName);
        return Files.readString(filePath).trim();
    }

    @BeforeAll
    public static void startMockServer() throws Exception {
        mockServer = new MockWebServer();
        mockUrl = mockServer.url("/").toString();

        var body = readFixture("page.html");
        var response = new MockResponse().setResponseCode(200).setBody(body);
        mockServer.enqueue(response);
    }

    @AfterAll
    public static void stopMockServer() throws IOException {
        mockServer.shutdown();
    }

    @BeforeEach
    public final void setUp() throws IOException, SQLException {
        app = App.getApp();
    }

    @Test
    public void testMainPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/");
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string())
                    .contains(strToUtf8("Анализатор страниц"))
                    .contains(strToUtf8("Ссылка"))
                    .contains(strToUtf8("Проверить"));
        });
    }

    @Test
    public void testUrlsPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls");
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains(strToUtf8("Сайты"));
        });
    }

    @Test
    public void testCreateUrl() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=https%3A%2F%2Fgithub.com%3A443%2Ffaraway10%2Fjava-project-72";
            var parsedUrl = "https://github.com:443";

            var response = client.post("/urls", requestBody);
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains(parsedUrl);

            var response2 = client.post("/urls", requestBody);
            assertThat(response2.code()).isEqualTo(200);

            var response3 = client.get("/urls");
            assertEquals(1, StringUtils.countMatches(
                    response3.body().string(), parsedUrl));

            var createdUrl = UrlRepository.findByName(parsedUrl).get();

            var response4 = client.get("/urls/" + createdUrl.getId());
            assertThat(response4.code()).isEqualTo(200);
            assertThat(response4.body().string()).contains(parsedUrl);
        });
    }

    @Test
    public void testCreateBadUrl() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=bad_url";
            var response = client.post("/urls", requestBody);
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).doesNotContain("bad_url");
            assertFalse(UrlRepository.existsByName("bad_url"));
        });
    }

    @Test
    void testUrlNotFound() throws Exception {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/999999");
            assertThat(response.code()).isEqualTo(404);
        });
    }

    @Test
    public void testCreateUrlCheck() {
        JavalinTest.test(app, (server, client) -> {
            var url = new Url(mockUrl);
            UrlRepository.save(url);
            try (var response = client.post("/urls/" + url.getId() + "/checks")) {
                var actualUrlCheck = UrlCheckRepository.getEntitiesByParentId(url.getId()).getFirst();

                assertThat(actualUrlCheck).isNotNull();
                assertThat(actualUrlCheck.getStatusCode()).isEqualTo(200);
                assertThat(actualUrlCheck.getTitle()).isEqualTo("Test title");
                assertThat(actualUrlCheck.getH1()).isEqualTo("Test header");
                assertThat(actualUrlCheck.getDescription()).isEqualTo("Test description");

                assertThat(response.body()).isNotNull();
                assertThat(response.code()).isEqualTo(200);
                assertThat(response.body().string())
                        .contains("Test title")
                        .contains("Test header")
                        .contains("Test description");
            }
        });
    }

    @Test
    void testFlashMessage() {
        JavalinTest.test(app, (server, client) -> {
            var baseUrl = "http://localhost:" + app.port();

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
            assertThat(content)
                    .contains("Страница успешно добавлена")
                    .contains("alert alert-success");

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
            assertThat(content)
                    .contains("Страница уже существует")
                    .contains("alert alert-info");

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
            assertThat(content)
                    .contains("Некорректный URL")
                    .contains("alert alert-danger");
        });
    }
}
