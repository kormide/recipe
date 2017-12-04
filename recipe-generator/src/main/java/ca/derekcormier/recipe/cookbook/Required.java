package ca.derekcormier.recipe.cookbook;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Map;

public class Required {
    private final String name;
    private final String type;
    private final boolean defaultProvided;
    private final Object defaultValue;

    @JsonCreator
    public Required(Map<String,Object> properties) {
        if (!properties.containsKey("name")) {
            throw new RuntimeException("required is missing name");
        }
        this.name = (String)properties.get("name");

        if (!properties.containsKey("type")) {
            throw new RuntimeException("required is missing type");
        }
        this.type = (String)properties.get("type");

        this.defaultProvided = properties.containsKey("default");
        this.defaultValue = properties.getOrDefault("default", null);

        if (CookbookUtils.isFlagType(type)) {
            throw new RuntimeException("required params cannot be of type flag");
        }
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Object getDefault() {
        return defaultValue;
    }

    public boolean hasDefault() {
        return defaultProvided;
    }
}
