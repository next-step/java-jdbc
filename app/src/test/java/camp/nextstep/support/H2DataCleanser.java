package camp.nextstep.support;

import java.sql.Connection;
import javax.sql.DataSource;

public class H2DataCleanser {

    public static void clear(DataSource dataSource, String tableName) {
        try (Connection connection = dataSource.getConnection()) {
            connection.prepareStatement("TRUNCATE TABLE " + tableName).executeUpdate();
            connection.prepareStatement("ALTER TABLE " + tableName + " ALTER COLUMN id RESTART WITH 1").executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
