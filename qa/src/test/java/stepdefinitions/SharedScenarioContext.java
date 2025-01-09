package stepdefinitions;

import java.util.HashMap;
import java.util.Map;

/**
 * Shared context to store and retrieve data across Cucumber steps within a single scenario.
 */
public class SharedScenarioContext {
    private static final ThreadLocal<SharedScenarioContext> instance = ThreadLocal.withInitial(SharedScenarioContext::new);
    private final Map<String, Object> data;

    private SharedScenarioContext() {
        this.data = new HashMap<>();
    }

    public static SharedScenarioContext getInstance() {
        return instance.get();
    }

    public void set(String key, Object value) {
        data.put(key, value);
    }

    public Object get(String key) {
        return data.get(key);
    }

    public <T> T get(String key, Class<T> type) {
        return type.cast(data.get(key));
    }

    public void clear() {
        data.clear();
    }
}