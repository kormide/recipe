package ca.derekcormier.recipe;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

import java.util.HashMap;
import java.util.Map;

public class Cake {
    private final Map<String,Object> entries = new HashMap<>();

    public void publish(String key, Object value) {
    }

    @JsonAnyGetter
    private Map<String,Object> getEntries() {
        return entries;
    }
}
