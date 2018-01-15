package ca.derekcormier.recipe;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.WRAPPER_OBJECT
)
public abstract class BaseIngredient {
    private final Map<String,Object> properties = new HashMap<>();

    @JsonIgnore
    private final String ingredientType;
    @JsonIgnore
    private final String domain;

    public BaseIngredient(String ingredientType, String domain) {
        this.ingredientType = ingredientType;
        this.domain = domain;
    }

    public BaseIngredient(String ingredientType) {
        this(ingredientType, "");
    }

    public String getDomain() {
        return domain;
    }

    public String getIngredientType() {
        return ingredientType;
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
            return (T)Array.newInstance(clazz.getComponentType(), 0);
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
