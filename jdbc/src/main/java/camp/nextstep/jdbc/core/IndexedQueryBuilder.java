package camp.nextstep.jdbc.core;

import java.util.StringJoiner;

public class IndexedQueryBuilder {

    private static final String PREPARED_KEY = "?";

    private final String tableName;
    private final StringBuilder query;

    public IndexedQueryBuilder(final String tableName) {
        this.tableName = tableName;
        query = new StringBuilder();
    }

    public IndexedQueryBuilder select(final String... columns) {
        query.append("SELECT ");
        if (columns.length == 0) {
            query.append("*");
            return this;
        }

        final StringJoiner stringJoiner = new StringJoiner(", ");
        for (final String column : columns) {
            stringJoiner.add(column);
        }
        query.append(stringJoiner)
                .append(" FROM ")
                .append(tableName);
        return this;
    }

    public IndexedQueryBuilder whereEq(final String... conditions) {
        query.append(" WHERE ");
        final StringJoiner stringJoiner = new StringJoiner(", ");
        for (final String condition : conditions) {
            stringJoiner.add(condition + " = " + PREPARED_KEY);
        }
        query.append(stringJoiner);
        return this;
    }

    public IndexedQueryBuilder insert(final String... values) {
        query.append("INSERT INTO ")
                .append(tableName)
                .append(createKeyClause(values))
                .append(" VALUES ")
                .append(createValueClause(values));
        return this;
    }

    private String createKeyClause(final String[] values) {
        final StringJoiner keyJoiner = new StringJoiner(", ", " (", ")");
        for (final String value : values) {
            keyJoiner.add(value);
        }
        return keyJoiner.toString();
    }

    private String createValueClause(final String[] values) {
        final StringJoiner valueJoiner = new StringJoiner(", ", "(", ")");
        for (int i = 0; i < values.length; i++) {
            valueJoiner.add(PREPARED_KEY);
        }
        return valueJoiner.toString();
    }

    public IndexedQueryBuilder update(final String... values) {
        query.append("UPDATE ")
                .append(tableName)
                .append(" SET ");
        final StringJoiner stringJoiner = new StringJoiner(", ");
        for (final String value : values) {
            stringJoiner.add(value + " = " + PREPARED_KEY);
        }
        query.append(stringJoiner);
        return this;
    }

    public IndexedQueryBuilder delete() {
        query.append("DELETE FROM ").append(tableName);
        return this;
    }

    public String build() {
        return query.toString();
    }
}
