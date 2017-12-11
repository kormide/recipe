package ca.derekcormier.recipe;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.CUSTOM,
    include = JsonTypeInfo.As.WRAPPER_OBJECT
)
@JsonTypeIdResolver(Ingredient.IngredientTypeIdResolver.class)
public abstract class Ingredient {
    protected static final Map<String, Class<? extends Ingredient>> ingredientRegister = new HashMap<>();
    private final String type;
    @JsonIgnore
    private final String domain;
    private final Map<String,Object> properties = new HashMap<>();

    public Ingredient(String type) {
        this(type, "");
    }

    public Ingredient(String type, String domain) {
        this.type = type;
        this.domain = domain;

        Ingredient.ingredientRegister.put(type, this.getClass());
    }

    public String getDomain() {
        return domain;
    }

    protected void setRequired(String name, Object value) {
        setProperty(name, value);
    }

    protected void setOptional(String name, boolean repeatable, Object value) {
        if (!repeatable) {
            setProperty(name, value);
        } else {
            List values = (List)properties.getOrDefault(name, new ArrayList());
            values.add(value);
            setProperty(name, values);
        }
    }

    protected void setCompoundOptional(String name, boolean repeatable, Object...keyValuePairs) {
        Map map = (Map)properties.getOrDefault(name, new HashMap());

        if (keyValuePairs.length % 2 != 0) {
            throw new IllegalArgumentException("must have an even number of key-value pairs for compound optional");
        }

        for (int i = 0; i < keyValuePairs.length; i += 2) {
            if (!(keyValuePairs[i] instanceof  String)) {
                throw new IllegalArgumentException("key in compound optional is not a string");
            }

            if (!repeatable) {
                map.put(keyValuePairs[i], keyValuePairs[i+1]);
            } else {
                List values = (List)map.getOrDefault(keyValuePairs[i], new ArrayList<>());
                values.add(keyValuePairs[i+1]);
                map.put(keyValuePairs[i], values);
            }
        }

        setProperty(name, map);
    }

    @JsonAnySetter
    private void setProperty(String key, Object value) {
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
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("could not serialize recipe to json", e);
        }
    }

    static protected class IngredientTypeIdResolver implements TypeIdResolver {
        private JavaType superType;

        @Override
        public void init(JavaType javaType) {
            superType = javaType;
        }

        @Override
        public String idFromValue(Object o) {
            return ((Ingredient)o).type;
        }

        @Override
        public JavaType typeFromId(DatabindContext databindContext, String s) throws IOException {
            // TODO: Deserialization here
            // return databindContext.constructSpecializedType(superType, /*class*/);
            return null;
        }

        @Override
        public String idFromValueAndType(Object o, Class<?> aClass) {
            return null;
        }

        @Override
        public String idFromBaseType() {
            return null;
        }

        @Override
        public String getDescForKnownTypeIds() {
            return null;
        }

        @Override
        public JsonTypeInfo.Id getMechanism() {
            return JsonTypeInfo.Id.CUSTOM;
        }
    }
}