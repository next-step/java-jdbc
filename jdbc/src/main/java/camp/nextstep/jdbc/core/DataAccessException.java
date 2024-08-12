package camp.nextstep.jdbc.core;

public class DataAccessException extends RuntimeException {
    public DataAccessException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public DataAccessException(final Throwable cause) {
        super(cause);
    }
}
