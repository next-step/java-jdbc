package support;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CleanUp {
    public static void cleanUp(final DataSource dataSource) {
        final String deleteQuery = "DELETE FROM users";
        final String initAiQuery = "TRUNCATE TABLE users RESTART IDENTITY;";

        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement pstmt1 = connection.prepareStatement(deleteQuery);
             final PreparedStatement pstmt2 = connection.prepareStatement(initAiQuery)) {

            pstmt1.executeUpdate();
            pstmt2.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
