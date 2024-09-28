package hexlet.code.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class Url {
    private long id;
    private String name;
    private LocalDateTime createdAt;
    private List<UrlCheck> urlChecks;
    public Url(String urlName) {
        name = urlName;
    }

    public String getCreatedAtFormatted() {
        return createdAt != null ? createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : null;
    }
}
