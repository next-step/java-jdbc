package camp.nextstep.jdbc.support;

import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;

public class DataSourceBean {
    public static DataSource dataSource() {
        final var jdbcDataSource = new JdbcDataSource();
        jdbcDataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;");
        jdbcDataSource.setUser("");
        jdbcDataSource.setPassword("");
        return jdbcDataSource;
    }
}
