package camp.nextstep.jdbc;

public class SqlDatabaseException extends RuntimeException {

  public SqlDatabaseException() {
  }

  public SqlDatabaseException(String message) {
    super(message);
  }
}
