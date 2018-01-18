package ca.derekcormier.recipe.cookbook;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ingredient {
    private final String name;
    private final boolean keyed;
    private final String defaultKey;
    private final List<Required> required;
    private final List<Initializer> initializers;
    private final List<Optional> optionals;
    private final Map<String,String> constants;

    @JsonCreator
    public Ingredient(
        @JsonProperty(value = "name", required = true) String name,
        @JsonProperty(value = "keyed") boolean keyed,
        @JsonProperty(value = "defaultKey") String defaultKey,
        @JsonProperty(value = "required") List<Required> required,
        @JsonProperty(value = "initializers") List<Initializer> initializers,
        @JsonProperty(value = "optionals") List<Optional> optionals,
        @JsonProperty(value = "constants") Map<String,String> constants
    ) {
        this.name = name;
        this.keyed = keyed;
        this.defaultKey = defaultKey;
        this.required = required == null ? new ArrayList<>() : required;
        this.initializers = initializers == null ? new ArrayList<>() : initializers;
        this.optionals = optionals == null ? new ArrayList<>() : optionals;
        this.constants = constants == null ? new HashMap<>() : constants;
    }

    public String getName() {
        return name;
    }

    public boolean isKeyed() {
        return keyed;
    }

    public String getDefaultKey() {
        return defaultKey;
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

    public Map<String,String> getConstants() {
        return constants;
    }
}
