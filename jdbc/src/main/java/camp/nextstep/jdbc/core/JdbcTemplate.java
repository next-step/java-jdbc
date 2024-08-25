package camp.nextstep.jdbc.core;

import camp.nextstep.dao.DataAccessException;
import camp.nextstep.jdbc.datasource.DataSourceUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcTemplate {

    private static final Logger log = LoggerFactory.getLogger(JdbcTemplate.class);

    private final DataSource dataSource;
    private final PreparedStatementSetter preparedStatementSetter;

    public JdbcTemplate(final DataSource dataSource) {
        this.dataSource = dataSource;
        this.preparedStatementSetter = new DefaultPreparedStatementSetter();
    }

    public void execute(final String sql, Object... args) {
        Connection connection = DataSourceUtils.getConnection(dataSource);

        try (final PreparedStatement preparedStatement =
            connection.prepareStatement(sql)) {
            preparedStatementSetter.setValues(preparedStatement, args);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            log.error("[Error] Query Exception has occured: " + sql, e);
            log.error("[Error] Args : " + Arrays.toString(args), e);
            throw new DataAccessException(e);
        }
    }

    public <T> T queryForObject(final String sql, final RowMapper<T> rowMapper, final Object arg) {
        try (final PreparedStatement preparedStatement =
            dataSource.getConnection().prepareStatement(sql)) {

            preparedStatementSetter.setValues(preparedStatement, arg);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return rowMapper.mapRow(resultSet);
                }
                return null;
            }

        } catch (Exception e) {
            log.error("[Error] Query Exception has occured: " + sql, e);
            throw new DataAccessException(e);
        }
    }

    public <T> List<T> query(final String sql, final RowMapper<T> rowMapper) {
        try (final ResultSet resultSet =
            dataSource.getConnection().prepareStatement(sql).executeQuery()) {
            final List<T> result = new ArrayList<>();

            while (resultSet.next()) {
                result.add(rowMapper.mapRow(resultSet));
            }
            return result;
        } catch (Exception e) {
            log.error("[Error] Query Exception has occured: " + sql, e);
            throw new DataAccessException(e);
        }
    }


}
