package camp.nextstep.jdbc.core;

import camp.nextstep.dao.DataAccessException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcTemplate {

    private static final int FIRST_INDEX = 1;
    private static final Logger log = LoggerFactory.getLogger(JdbcTemplate.class);

    private final DataSource dataSource;

    public JdbcTemplate(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void execute(final String sql, Object... args) {
        try (final PreparedStatement preparedStatement =
            dataSource.getConnection().prepareStatement(sql)) {

            for (int i = FIRST_INDEX; i <= args.length; i++) {
                preparedStatement.setObject(i, args[i - 1]);
            }

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

            preparedStatement.setObject(FIRST_INDEX, arg);

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
