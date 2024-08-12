package camp.nextstep.jdbc.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IndexedQueryBuilderTest {

    @Test
    @DisplayName("index 기반 SELECT 쿼리를 생성할 수 있다.")
    void testSelectQuery() {
        final IndexedQueryBuilder builder = new IndexedQueryBuilder("users");

        final String query = builder.select("id", "password").whereEq("id").build();

        assertThat(query).isEqualTo("SELECT id, password FROM users WHERE id = ?");
    }

    @Test
    @DisplayName("index 기반 INSERT 쿼리를 생성할 수 있다.")
    void testInsertQuery() {
        final IndexedQueryBuilder builder = new IndexedQueryBuilder("users");

        final String query = builder.insert("account", "password", "email").build();

        assertThat(query).isEqualTo("INSERT INTO users (account, password, email) VALUES (?, ?, ?)");
    }

    @Test
    @DisplayName("index 기반 UPDATE 쿼리를 생성할 수 있다.")
    void testUpdateQuery() {
        final IndexedQueryBuilder builder = new IndexedQueryBuilder("users");

        final String query = builder.update("password", "email").whereEq("id").build();

        assertThat(query).isEqualTo("UPDATE users SET password = ?, email = ? WHERE id = ?");
    }

    @Test
    @DisplayName("index 기반 DELETE 쿼리를 생성할 수 있다.")
    void testDeleteQuery() {
        final IndexedQueryBuilder builder = new IndexedQueryBuilder("users");

        final String query = builder.delete().whereEq("id").build();

        assertThat(query).isEqualTo("DELETE FROM users WHERE id = ?");
    }

}
