package camp.nextstep.dao;

public class EmptyResultDataAccessException extends DataAccessException {

  private static final long serialVersionUID = 1L;

  public EmptyResultDataAccessException(String message) {
    super(message);
  }

}
