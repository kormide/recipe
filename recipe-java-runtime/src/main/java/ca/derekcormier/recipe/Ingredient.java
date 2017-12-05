package ca.derekcormier.recipe;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.CUSTOM,
    include = JsonTypeInfo.As.WRAPPER_OBJECT
)
@JsonTypeIdResolver(Ingredient.IngredientTypeIdResolver.class)
public abstract class Ingredient {
    private final String type;
    private final Map<String,Object> properties = new HashMap<>();

    public Ingredient(String type) {
        this.type = type;
    }

    protected void setRequired(String name, Object value) {
        setProperty(name, value);
    }

    protected void setOptional(String name, Object value) {
        setProperty(name, value);
    }

    protected void setCompoundOptional(String name, Object...keyValuePairs) {
        Map map = new HashMap();

        if (keyValuePairs.length % 2 != 0) {
            throw new IllegalArgumentException("must have an even number of key-value pairs for compound optional");
        }

        for (int i = 0; i < keyValuePairs.length; i += 2) {
            if (!(keyValuePairs[i] instanceof  String)) {
                throw new IllegalArgumentException("key in compound optional is not a string");
            }

            map.put(keyValuePairs[i], keyValuePairs[i+1]);
        }

        setProperty(name, map);
    }

    private void setProperty(String key, Object value) {
        properties.put(key, value);
    }

    @JsonAnyGetter
    protected Map<String,Object> getProperties() {
        return properties;
    }

    static protected class IngredientTypeIdResolver implements TypeIdResolver {
        @Override
        public void init(JavaType javaType) {
        }

        @Override
        public String idFromValue(Object o) {
            return ((Ingredient)o).type;
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
        public JavaType typeFromId(DatabindContext databindContext, String s) throws IOException {
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