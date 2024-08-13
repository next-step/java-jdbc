package camp.nextstep.jdbc.core;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.function.TriConsumer;

public enum PreparedStatementParameterSetter {
    STRING(String.class, (preparedStatement, index, value) -> {
        try {
            preparedStatement.setString(index, (String) value);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }),
    LONG(Long.class, (preparedStatement, index, value) -> {
        try {
            preparedStatement.setLong(index, (Long) value);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }),
    INTEGER(Integer.class, (preparedStatement, index, value) -> {
        try {
            preparedStatement.setInt(index, (Integer) value);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }),
    OBJECT(Object.class, (preparedStatement, index, value) -> {
        try {
            preparedStatement.setObject(index, value);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }),
    BOOLEAN(Boolean.class, (preparedStatement, index, value) -> {
        try {
            preparedStatement.setBoolean(index, (Boolean) value);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    });

    private final Class<?> clazz;
    private final TriConsumer<PreparedStatement, Integer, Object> setter;

    private static final Map<Class<?>, PreparedStatementParameterSetter> TYPE_MAP =
        Arrays.stream(PreparedStatementParameterSetter.values())
            .collect(Collectors.toMap(type -> type.clazz, type -> type));

    PreparedStatementParameterSetter(Class<?> clazz, TriConsumer<PreparedStatement, Integer, Object> setter) {
        this.clazz = clazz;
        this.setter = setter;
    }

    public static void setParameter(PreparedStatement preparedStatement, int index, Object value) {
        PreparedStatementParameterSetter parameterSetter = TYPE_MAP.getOrDefault(value.getClass(), OBJECT);
        parameterSetter.accept(preparedStatement, index, value);
    }

    public void accept(PreparedStatement preparedStatement, int index, Object value) {
        setter.accept(preparedStatement, index, value);
    }
}
