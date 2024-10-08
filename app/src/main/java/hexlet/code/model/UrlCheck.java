package hexlet.code.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public final class UrlCheck {
    private long id;
    private int statusCode;
    private String title;
    private String h1;
    private String description;
    private long urlId;
    private LocalDateTime createdAt;

    public UrlCheck(
            int checkStatusCode,
            String checkTitle,
            String checkH1,
            String checkDescription
    ) {
        statusCode = checkStatusCode;
        title = checkTitle;
        h1 = checkH1;
        description = checkDescription;
    }
}
