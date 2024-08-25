package camp.nextstep.jdbc;

public class IncorrectResultSizeDataAccessException extends SqlDatabaseException{

  public IncorrectResultSizeDataAccessException(String message) {
    super(message);
  }
}
