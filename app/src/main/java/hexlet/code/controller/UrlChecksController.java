package hexlet.code.controller;

import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.sql.SQLException;
import java.util.NoSuchElementException;

public class UrlChecksController {
    public static void create(Context ctx) throws SQLException {
        long urlId = ctx.pathParamAsClass("id", Long.class).get();
        var url = UrlRepository.find(urlId).orElseThrow(NoSuchElementException::new);

        try {
            HttpResponse<String> response = Unirest.get(url.getName()).asString();
            String htmlContent = response.getBody();
            Document document = Jsoup.parse(htmlContent);

            var urlCheck = new UrlCheck(
                    response.getStatus(),
                    document.title(),
                    document.select("h1").text(),
                    document.select("meta[name=description]").attr("content")
            );

            urlCheck.setUrlId(urlId);
            UrlCheckRepository.save(urlCheck);

            ctx.sessionAttribute("flash", "Страница успешно проверена");
            ctx.sessionAttribute("flashType", "success");
        } catch (UnirestException e) {
            ctx.sessionAttribute("flash", "Некорректный адрес");
            ctx.sessionAttribute("flashType", "danger");
        } catch (Exception e) {
            ctx.sessionAttribute("flash", e.getMessage());
            ctx.sessionAttribute("flashType", "danger");
        }

        ctx.redirect(NamedRoutes.urlPath(urlId));
    }
}
