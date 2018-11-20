package ca.derekcormier.recipe;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.WRAPPER_OBJECT
)
public abstract class Ingredient {
    private final Map<String,Object> properties = new HashMap<>();

    @JsonIgnore
    private final String ingredientType;
    @JsonIgnore
    private final String domain;

    public Ingredient(String ingredientType, String domain) {
        this.ingredientType = ingredientType;
        this.domain = domain;
    }

    public Ingredient(String ingredientType) {
        this(ingredientType, "");
    }

    protected Ingredient(Ingredient other) {
        this(other.getIngredientType(), other.getDomain());
        other.getProperties().forEach((key, value) -> {
            this.setProperty(key, value);
        });
    }

    public String getDomain() {
        return domain;
    }

    public String getIngredientType() {
        return ingredientType;
    }

    protected void setRequired(String name, Object value) {
        setProperty(name, value);
    }

    protected void setOptional(String name, boolean repeatable, Object value) {
        if (!repeatable) {
            setProperty(name, value);
        }
        else {
            List values = (List)getProperties().getOrDefault(name, new ArrayList());
            values.add(value);
            setProperty(name, values);
        }
    }

    protected void setCompoundOptional(String name, boolean repeatable, Object...keyValuePairs) {
        if (keyValuePairs.length % 2 != 0) {
            throw new IllegalArgumentException("must have an even number of key-value pairs for compound optional");
        }

        for (int i = 0; i < keyValuePairs.length; i += 2) {
            if (!(keyValuePairs[i] instanceof String)) {
                throw new IllegalArgumentException("key in compound optional is not a string");
            }
        }

        if (!repeatable) {
            Map map = (Map)getProperties().getOrDefault(name, new HashMap());
            for (int i = 0; i < keyValuePairs.length; i += 2) {
                map.put(keyValuePairs[i], keyValuePairs[i+1]);
            }
            setProperty(name, map);
        }
        else {
            List<Map> list = (List)getProperties().getOrDefault(name, new ArrayList<>());
            Map map = new HashMap();
            for (int i = 0; i < keyValuePairs.length; i += 2) {
                map.put(keyValuePairs[i], keyValuePairs[i+1]);
            }
            list.add(map);
            setProperty(name, list);
        }
    }

    @JsonAnySetter
    protected void setProperty(String key, Object value) {
        properties.put(key, value);
    }

    protected <T> T getProperty(String key) {
        return (T)properties.get(key);
    }

    protected <T> T getProperty(Class<T> clazz, String key) {
        if (hasProperty(key)) {
            return (T)getProperty(key);
        }
        else if (clazz == int.class) {
            return (T)new Integer(0);
        }
        else if (clazz == float.class) {
            return (T)new Float(0.0f);
        }
        else if (clazz == boolean.class) {
            return (T)new Boolean(false);
        }
        else if (clazz == String.class) {
            return null;
        }
        else if (clazz.isEnum()) {
            return clazz.getEnumConstants()[0];
        }
        else if (clazz.isArray()) {
            return (T) Array.newInstance(clazz.getComponentType(), 0);
        }

        return null;
    }

    protected boolean hasProperty(String key) {
        return properties.containsKey(key);
    }

    @JsonAnyGetter
    protected Map<String,Object> getProperties() {
        return properties;
    }
}