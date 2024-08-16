package camp.nextstep.transaction;

@FunctionalInterface
public interface TransactionCallback {
    void execute();
}
