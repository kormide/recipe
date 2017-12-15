package ca.derekcormier.recipe;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.HashMap;
import java.util.Map;

public class Cake {
    private final Map<String,Object> entries = new HashMap<>();

    public void publish(String key, Object value) {
        entries.put(key, value);
    }

    public <T> T get(String key) {
        return (T)entries.get(key);
    }

    @JsonAnyGetter
    private Map<String,Object> getEntries() {
        return entries;
    }

    @JsonAnySetter
    private void setEntry(String key, Object value) {
        entries.put(key, value);
    }
}
