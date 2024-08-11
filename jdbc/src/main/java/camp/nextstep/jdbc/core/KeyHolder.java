package camp.nextstep.jdbc.core;

import java.util.HashMap;
import java.util.Map;

public class KeyHolder {

    private final Map<String, Object> keys = new HashMap<>();

    public Number getKey() {
        final Object key = keys.values().stream().findFirst().orElse(null);
        if (key instanceof Number) {
            return (Number) key;
        }
        return null;
    }

    public Map<String, Object> getKeys() {
        return keys;
    }

    public void addGeneratedKeys(final String keyName, final Number key) {
        keys.put(keyName, key);
    }

    public void addGeneratedKeys(final Map<String, Object> keys) {
        this.keys.putAll(keys);
    }
}
