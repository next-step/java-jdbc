package camp.nextstep.jdbc.core;

import camp.nextstep.dao.DataAccessException;

import javax.annotation.Nullable;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * ResultSet 을 받아서 원하는 타입으로 변환하는 함수형 인터페이스
 * <p>
 * org.springframework.jdbc.core.ResultSetExtractor 를 가져왔습니다.
 */
@FunctionalInterface
public interface ResultSetExtractor<T> {
    @Nullable
    T extractData(ResultSet rs) throws SQLException, DataAccessException;
}
