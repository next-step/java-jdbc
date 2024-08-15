package camp.nextstep.transaction.support;

import camp.nextstep.jdbc.core.DataAccessException;

public class TransactionException extends DataAccessException {

    public TransactionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
