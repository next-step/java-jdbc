package camp.nextstep.jdbc.core;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DataSourceUtils {
    private static final ThreadLocal<Connection> CONNECTION_HOLDER = new ThreadLocal<>();

    public static Connection getConnection(final DataSource dataSource) throws SQLException {
        Connection connection = CONNECTION_HOLDER.get();

        if (connection == null || connection.isClosed()) {
            connection = dataSource.getConnection();
            CONNECTION_HOLDER.set(connection);
        }

        return connection;
    }
}
