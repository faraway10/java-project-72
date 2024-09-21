package hexlet.code.model;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Url {
    private long id;
    private String name;
    private Timestamp createdAt;

    public Url(String urlName) {
        name = urlName;
    }
}
