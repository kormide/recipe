package ca.derekcormier.recipe;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

import java.util.HashMap;
import java.util.Map;

public abstract class Ingredient {
    private final String type;
    private final Map<String,Object> properties = new HashMap<>();

    public Ingredient(String type) {
        this.type = type;
    }

    protected void setProperty(String key, Object value) {
        properties.put(key, value);
    }

    protected Object getProperty(String key) {
        return properties.get(key);
    }

    @JsonAnyGetter
    protected Map<String,Object> getProperties() {
        Map<String,Object> wrappedProperties = new HashMap<>();
        wrappedProperties.put(type, properties);
        return wrappedProperties;
    }
}
