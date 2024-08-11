package camp.nextstep.jdbc.sql;

import java.sql.Types;
import java.util.Arrays;
import java.util.List;

public enum SqlType {

    INTEGER(Types.INTEGER, List.of(Integer.class, int.class)),
    LONG(Types.BIGINT, List.of(Long.class, long.class)),
    VAR_CHAR(Types.VARCHAR, List.of(String.class)),
    ;

    private final int sqlTypeValue;
    private final List<Class<?>> javaTypeValues;

    SqlType(int sqlTypeValue, List<Class<?>> javaTypeValues) {
        this.sqlTypeValue = sqlTypeValue;
        this.javaTypeValues = javaTypeValues;
    }

    public static SqlType from(int sqlTypeValue) {
        return Arrays.stream(values())
                .filter(value -> value.sqlTypeValue == sqlTypeValue)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 sql 타입입니다."));
    }

    public boolean isSqlType(Object object) {
        return javaTypeValues.stream()
                .anyMatch(value -> value.isAssignableFrom(object.getClass()));
    }
}
