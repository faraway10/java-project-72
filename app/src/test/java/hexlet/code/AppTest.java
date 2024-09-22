package hexlet.code;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import hexlet.code.repository.UrlRepository;
import org.apache.commons.lang3.StringUtils;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.sql.SQLException;

public class AppTest {
    private Javalin app;

    // Без использования этого метода на Windows в тестах сбивается кодировка кириллицы
    private static String strToUtf8(String str) {
        return new String(str.getBytes(), UTF_8);
    }

    @BeforeEach
    public final void setUp() throws IOException, SQLException {
        App.setTestingMode();
        app = App.getApp();
    }

    @Test
    public void testMainPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/");
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains(strToUtf8("Анализатор страниц"));
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

            var response = client.post("/urls", requestBody);
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("https://github.com:443");
            assertTrue(UrlRepository.existsByName("https://github.com:443"));

            var response2 = client.post("/urls", requestBody);
            assertThat(response2.code()).isEqualTo(200);

            var response3 = client.get("/urls");
            assertEquals(1, StringUtils.countMatches(
                    response3.body().string(), "https://github.com:443"));

            var response4 = client.get("/urls/1");
            assertThat(response4.code()).isEqualTo(200);
            assertThat(response4.body().string()).contains("https://github.com:443");
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
}
