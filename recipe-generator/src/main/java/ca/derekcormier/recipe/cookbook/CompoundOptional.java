package ca.derekcormier.recipe.cookbook;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CompoundOptional {
    private final String name;
    private final List<Param> params;
    private final boolean repeatable;

    @JsonCreator
    public CompoundOptional(
        @JsonProperty(value = "name", required = true) String name,
        @JsonProperty(value = "params", required = true) List<Param> params,
        @JsonProperty(value = "repeatable") boolean repeatable
    ) {
        this.name = name;
        this.params = params;
        this.repeatable = repeatable;
    }

    public String getName() {
        return name;
    }

    public List<Param> getParams() {
        return params;
    }

    public boolean isRepeatable() {
        return repeatable;
    }
}
