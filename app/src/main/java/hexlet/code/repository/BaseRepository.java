package hexlet.code.repository;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.Setter;

public class BaseRepository {
    @Getter
    @Setter
    private static HikariDataSource dataSource;
}
