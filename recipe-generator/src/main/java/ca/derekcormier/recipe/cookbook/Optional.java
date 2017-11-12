package ca.derekcormier.recipe.cookbook;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Optional {
    private final String name;
    private final Type type;
    private final boolean repeatable;

    @JsonCreator
    public Optional(
        @JsonProperty(value = "name", required = true) String name,
        @JsonProperty(value = "type", required = true) Type type,
        @JsonProperty(value = "repeatable") boolean repeatable
    ) {
        this.name = name;
        this.type = type;
        this.repeatable = repeatable;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public boolean isRepeatable() {
        return repeatable;
    }
}
