package ca.derekcormier.recipe.cookbook;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Required {
    private final String name;
    private final Type type;
    private final Object defaultValue;

    @JsonCreator
    public Required(
        @JsonProperty(value = "name", required = true) String name,
        @JsonProperty(value = "type", required = true) Type type,
        @JsonProperty(value = "defaultValue") Object defaultValue
    ) {
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }
}
