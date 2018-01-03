package ca.derekcormier.recipe.cookbook;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Ingredient {
    private final String name;
    private final boolean keyed;
    private final List<Required> required;
    private final List<Initializer> initializers;
    private final List<Optional> optionals;
    private final List<String> keyConstants;

    @JsonCreator
    public Ingredient(
        @JsonProperty(value = "name", required = true) String name,
        @JsonProperty(value = "keyed") boolean keyed,
        @JsonProperty(value = "required") List<Required> required,
        @JsonProperty(value = "initializers") List<Initializer> initializers,
        @JsonProperty(value = "optionals") List<Optional> optionals,
        @JsonProperty(value = "keyConstants") List<String> keyConstants
    ) {
        this.name = name;
        this.keyed = keyed;
        this.required = required == null ? new ArrayList<>() : required;
        this.initializers = initializers == null ? new ArrayList<>() : initializers;
        this.optionals = optionals == null ? new ArrayList<>() : optionals;
        this.keyConstants = keyConstants == null ? new ArrayList<>() : keyConstants;
    }

    public String getName() {
        return name;
    }

    public boolean isKeyed() {
        return keyed;
    }

    public List<Required> getRequired() {
        return required;
    }

    public List<Initializer> getInitializers() {
        return initializers;
    }

    public List<Optional> getOptionals() {
        return optionals;
    }

    public List<String> getKeyConstants() {
        return keyConstants;
    }
}
