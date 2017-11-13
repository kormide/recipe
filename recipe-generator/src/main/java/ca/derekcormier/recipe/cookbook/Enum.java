package ca.derekcormier.recipe.cookbook;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Enum {
    private final String name;
    private final List<String> values;

    @JsonCreator
    public Enum(
        @JsonProperty(value = "name", required = true) String name,
        @JsonProperty(value = "values", required = true) List<String> values
    ) {
        this.name = name;
        this.values = values;
    }

    public String getName() {
        return name;
    }

    public List<String> getValues() {
        return values;
    }
}
