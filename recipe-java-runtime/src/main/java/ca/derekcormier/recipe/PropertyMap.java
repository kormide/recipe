package ca.derekcormier.recipe;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public abstract class PropertyMap {
    private final static ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String,Object> properties = new HashMap<>();

    @JsonAnySetter
    protected void setProperty(String key, Object value) {
        properties.put(key, value);
    }

    protected <T> T getProperty(String key) {
        return (T)properties.get(key);
    }

    protected boolean hasProperty(String key) {
        return properties.containsKey(key);
    }

    @JsonAnyGetter
    protected Map<String,Object> getProperties() {
        return properties;
    }

    public String toJson() {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("could not serialize recipe to json", e);
        }
    }

}
