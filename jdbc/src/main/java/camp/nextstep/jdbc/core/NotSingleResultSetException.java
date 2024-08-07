package camp.nextstep.jdbc.core;

public class NotSingleResultSetException extends RuntimeException {
    public NotSingleResultSetException() {
        super("쿼리 결과가 2건이상일 수 없습니다.");
    }
}
