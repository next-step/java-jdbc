package camp.nextstep.jdbc.core;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * ResultSet 을 받아서 원하는 타입으로 변환하는 함수형 인터페이스
 * <p>
 * org.springframework.jdbc.core.RowMapper 를 가져왔습니다.
 */
@FunctionalInterface
public interface RowMapper<T> {
    T mapRow(final ResultSet rs) throws SQLException;
}
