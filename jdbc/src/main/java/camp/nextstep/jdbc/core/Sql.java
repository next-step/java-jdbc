package camp.nextstep.jdbc.core;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Sql {
    private static final Logger log = LoggerFactory.getLogger(Sql.class);

    private final String query;
    private final List<Object> params;

    public Sql(String query) {
        this(query, List.of());
    }

    public Sql(String query, Object param) {
        this(query, List.of(param));
    }

    public Sql(String query, List<Object> params) {
        printLog(query, params);
        this.query = query;
        this.params = params;
    }

    private void printLog(String query, Object param) {
        log.debug("query : {}, params : {}", query, param);
    }

    public String getQuery() {
        return query;
    }

    public List<?> getParams() {
        return params;
    }
}
