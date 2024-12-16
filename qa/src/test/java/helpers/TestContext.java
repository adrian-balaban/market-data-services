package helpers;

import java.util.HashMap;
import java.util.Map;

public class TestContext {
    private Map<String, Object> sharedData = new HashMap<>();

    public void set(String key, Object value) {
        sharedData.put(key, value);
    }

    public Object get(String key) {
        return sharedData.get(key);
    }
}